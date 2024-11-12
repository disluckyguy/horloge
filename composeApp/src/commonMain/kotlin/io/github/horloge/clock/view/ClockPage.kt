package io.github.horloge.clock.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.horloge.clock.domain.ClockViewModel
import io.github.horloge.theme.onSecondaryDark
import io.github.horloge.theme.onSecondaryLight
import io.github.horloge.theme.outlineDark
import io.github.horloge.theme.outlineLight
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClockPage(modifier: Modifier = Modifier, viewModel: ClockViewModel = koinViewModel()) {
    val state = viewModel.uiState.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
            Canvas(modifier = Modifier) {
                drawCircle(
                    color = if (isDarkTheme) {
                        onSecondaryDark
                    } else onSecondaryLight,
                    radius = 100.dp.toPx(),
                    style = Fill,
                )
            }
            Canvas(modifier = Modifier) {
                drawCircle(
                    color = if (isDarkTheme) {
                        outlineDark
                    } else outlineLight,
                    radius = 105f.dp.toPx(),
                    style = Stroke(width = 5f),
                )
            }
            Text(
                text = "${state.value.localTime.time.hour}:${state.value.localTime.time.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
        }

        val (city, continent) = state.value.timeZone.id.split("/")
        Text("$city, $continent", style = MaterialTheme.typography.displaySmall)
    }
}