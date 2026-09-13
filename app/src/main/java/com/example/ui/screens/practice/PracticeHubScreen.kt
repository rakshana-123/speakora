package com.example.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.ui.components.ExerciseItemCard
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.CoachViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeHubScreen(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    val allExercises by viewModel.allExercises.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredExercises = allExercises.filter { exercise ->
        val matchesCategory = selectedCategory == null || exercise.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                exercise.title.contains(searchQuery, ignoreCase = true) ||
                exercise.description.contains(searchQuery, ignoreCase = true) ||
                exercise.category.displayName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("practice_hub_screen")
    ) {
        // Title & description
        Text(
            text = "Communication Gym",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "11 targeted disciplines to master verbal & written impact",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search scenarios, drills, vocabulary...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("practice_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            val isAllSelected = selectedCategory == null
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAllSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { viewModel.setCategoryFilter(null) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("category_filter_all"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All Modules (${allExercises.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            ExerciseCategory.values().forEach { category ->
                val isSelected = selectedCategory == category
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setCategoryFilter(if (isSelected) null else category) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("category_filter_${category.name}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category.displayName,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exercise List
        if (filteredExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No practice exercises match your filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredExercises, key = { it.id }) { exercise ->
                    ExerciseItemCard(
                        exercise = exercise,
                        onClick = { viewModel.selectExercise(exercise) }
                    )
                }
            }
        }
    }
}
