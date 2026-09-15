package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.appupdate.UpdateState
import com.example.ui.screens.challenges.ChallengesScreen
import com.example.ui.screens.coach.AiCoachScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.login.LoginScreen
import com.example.ui.screens.practice.PracticeHubScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.workout.ExercisePlayerScreen
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.CoachViewModel

enum class NavigationTab(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "tab_home"),
    PRACTICE("Practice", Icons.Default.FitnessCenter, "tab_practice"),
    PROGRESS("Progress", Icons.Default.Insights, "tab_progress"),
    CHALLENGES("Social", Icons.Default.Groups, "tab_challenges"),
    COACH("AI Coach", Icons.Default.AutoAwesome, "tab_coach"),
    PROFILE("Profile", Icons.Default.Person, "tab_profile")
}

@Composable
fun MainAppContainer(
    viewModel: CoachViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    val activeExercise by viewModel.activeExercise.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val authBusy by viewModel.authBusy.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    // Look for a newer release every time the app starts (post-login).
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) viewModel.checkForUpdates()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (authState) {
            AuthState.Loading, AuthState.LoggedOut -> LoginScreen(
                isLoading = authState == AuthState.Loading || authBusy,
                errorMessage = authError,
                onLogin = viewModel::login,
                onSignup = viewModel::signup
            )

            is AuthState.LoggedIn -> MainTabs(
                viewModel = viewModel,
                currentTab = currentTab,
                onTabChange = { currentTab = it }
            )
        }

        // In-app update prompt
        val available = updateState as? UpdateState.Available
        if (available != null) {
            AlertDialog(
                onDismissRequest = { /* remind on next check */ },
                title = { Text("Update available 🎉") },
                text = {
                    Text(
                        "Version ${available.info.versionName} is out.\n\n${available.info.changelog}"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.downloadUpdate(available.info) }) {
                        Text("Download & install", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissUpdateForSession() }) {
                        Text("Later")
                    }
                }
            )
        }

        // Focused Fullscreen Interactive Exercise Player Overlay
        AnimatedVisibility(
            visible = activeExercise != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            activeExercise?.let { exercise ->
                ExercisePlayerScreen(
                    exercise = exercise,
                    viewModel = viewModel,
                    onClose = { viewModel.closeExercise() }
                )
            }
        }
    }
}

@Composable
private fun MainTabs(
    viewModel: CoachViewModel,
    currentTab: NavigationTab,
    onTabChange: (NavigationTab) -> Unit
) {
    Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("main_bottom_nav_bar")
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onTabChange(tab) },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    NavigationTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToPractice = { onTabChange(NavigationTab.PRACTICE) },
                        onNavigateToCoachChat = { onTabChange(NavigationTab.COACH) },
                        onOpenRecovery = { onTabChange(NavigationTab.CHALLENGES) }
                    )
                    NavigationTab.PRACTICE -> PracticeHubScreen(
                        viewModel = viewModel
                    )
                    NavigationTab.PROGRESS -> ProgressScreen(
                        viewModel = viewModel
                    )
                    NavigationTab.CHALLENGES -> ChallengesScreen(
                        viewModel = viewModel
                    )
                    NavigationTab.COACH -> AiCoachScreen(
                        viewModel = viewModel
                    )
                    NavigationTab.PROFILE -> ProfileScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
