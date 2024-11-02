package io.github.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.delay

@Composable
fun StopwatchPage(modifier: Modifier = Modifier, windowSizeClass: WindowSizeClass) {
    val wideMode = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    var paused by remember { mutableStateOf(true) }
    var time by remember { mutableStateOf(0L) }
    var timeMillis by remember { mutableStateOf(0L) }
    val rankings = remember { mutableStateListOf<Long>() }


    LaunchedEffect(1) {
        while (true) {
            if (!paused) {
                time += 1000
                timeMillis = 0
            }
            delay(1000)
        }
    }

    LaunchedEffect(2) {
        while (true) {
            if (!paused) {
                timeMillis += 10
            }
            delay(10)
        }
    }

    if (wideMode) {

        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = formatTimeMillis(time + timeMillis),
                textAlign = TextAlign.Center,
                style = typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f).padding(12.dp)
            )

            LazyColumn(
                modifier.weight(3f)
                    .fadingEdge(Brush.verticalGradient(0.7f to Color.Red, 1f to Color.Transparent))
            ) {
                for ((i, ranking) in rankings.reversed().withIndex()) {
                    item {
                        Text(
                            "#${rankings.size - i} ${formatTimeMillis(ranking)}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            StopwatchControlsWide(
                time = time,
                onTimeChange = { time = it },
                paused = paused,
                onPausedChange = { paused = it },
                rankings = rankings,
                timeMillis = timeMillis,
                onTimeMillisChange = { timeMillis = it },
            )
        }
    } else {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = formatTimeMillis(time + timeMillis),
                textAlign = TextAlign.Center,
                style = typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )

            LazyColumn(
                modifier.weight(3f)
                    .fadingEdge(Brush.verticalGradient(0.7f to Color.Red, 1f to Color.Transparent))
            ) {
                for ((i, ranking) in rankings.reversed().withIndex()) {
                    item {
                        Text(
                            "#${rankings.size - i} ${formatTimeMillis(ranking)}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            StopwatchControlsCompact(
                time = time,
                onTimeChange = { time = it },
                paused = paused,
                onPausedChange = { paused = it },
                rankings = rankings,
                timeMillis = timeMillis,
                onTimeMillisChange = { timeMillis = it },
            )
        }
    }
}


@Composable
fun StopwatchControlsWide(
    time: Long,
    onTimeChange: (Long) -> Unit,
    timeMillis: Long,
    onTimeMillisChange: (Long) -> Unit,
    paused: Boolean,
    onPausedChange: (Boolean) -> Unit,
    rankings: MutableList<Long>
) {


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalIconButton(
            onClick = {
                onPausedChange(true)
                onTimeChange(0L)
                onTimeMillisChange(0L)
                rankings.clear()
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(50.dp)
        ) {
            Icon(
                Icons.Default.RestartAlt,
                contentDescription = "Restart Stopwatch"
            )
        }


        LargeFloatingActionButton(
            onClick = {
                onPausedChange(!paused)
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(80.dp)
        ) {
            Icon(
                if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = "Play"
            )
        }


        FilledTonalIconButton(
            onClick = {
                rankings.add(time + timeMillis)
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(50.dp)
        ) {
            Icon(
                Icons.Rounded.Timer,
                contentDescription = "Add Ranking"
            )
        }
    }
}

@Composable
fun StopwatchControlsCompact(
    time: Long,
    onTimeChange: (Long) -> Unit,
    timeMillis: Long,
    onTimeMillisChange: (Long) -> Unit,
    paused: Boolean,
    onPausedChange: (Boolean) -> Unit,
    rankings: MutableList<Long>
) {

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilledTonalIconButton(
            onClick = {
                onPausedChange(true)
                onTimeChange(0L)
                onTimeMillisChange(0L)
                rankings.clear()
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(50.dp)
        ) {
            Icon(
                Icons.Default.RestartAlt,
                contentDescription = "Restart Stopwatch"
            )
        }


        LargeFloatingActionButton(
            onClick = {
                onPausedChange(!paused)
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(80.dp)
        ) {
            Icon(
                if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = "Play"
            )
        }


        FilledTonalIconButton(
            onClick = {
                rankings.add(time + timeMillis)
            },
            shape = CircleShape,
            modifier = Modifier.requiredSize(50.dp)
        ) {
            Icon(
                Icons.Rounded.Timer,
                contentDescription = "Add Ranking"
            )
        }
    }
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }