package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan

@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val barCount = 24
    val displayAmps = if (amplitudes.isNotEmpty()) {
        amplitudes.takeLast(barCount).let { list ->
            if (list.size < barCount) {
                List(barCount - list.size) { 0.15f } + list
            } else list
        }
    } else {
        List(barCount) { 0.15f }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        displayAmps.forEachIndexed { index, amp ->
            val dynamicHeight = if (isRecording) {
                val factor = (amp * (0.6f + (0.4f * waveOffset))).coerceIn(0.1f, 1.0f)
                (factor * 56).coerceAtLeast(6f).dp
            } else {
                6.dp
            }

            val barColor = if (isRecording) {
                if (index % 2 == 0) PrimaryIndigo else SecondaryCyan
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )

            if (index < barCount - 1) {
                Box(modifier = Modifier.width(4.dp))
            }
        }
    }
}
