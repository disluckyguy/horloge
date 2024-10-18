package io.github.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.draw.clip
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
    val rankings = remember { mutableStateListOf<Long>() }

    LaunchedEffect(1) {
        while (true) {
            if (!paused) {
                time += 1000
            }
            delay(1000)
        }
    }

    LazyVerticalStaggeredGrid(columns = if (wideMode) StaggeredGridCells.Adaptive(360.dp) else StaggeredGridCells.Fixed(1)) {
        item {
            Text(
                text = formatTime(time),
                textAlign = TextAlign.Center,
                style = typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Box(modifier.height(300.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                LazyColumn {
                    for ((i, ranking) in rankings.withIndex()) {
                        item {
                            Text("#${i + 1} ${formatTime(ranking)}")
                        }
                    }
                }
            }
        }
        item {
            Box(modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                LazyHorizontalGrid(rows = if (wideMode) GridCells.Fixed(1) else GridCells.Fixed(3), modifier = Modifier.padding(12.dp)) {
                    item {
                        SmallFloatingActionButton(
                            onClick = {
                                paused = true
                                time = 0L
                                rankings.clear()
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Default.RestartAlt,
                                contentDescription = "Restart Stopwatch"
                            )
                        }
                    }
                    item {
                        LargeFloatingActionButton(
                            onClick = {
                                paused = !paused
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Icon(
                                if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Play"
                            )
                        }
                    }
                    item {
                        SmallFloatingActionButton(
                            onClick = {
                                rankings.add(time)
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Timer,
                                contentDescription = "Add Ranking"
                            )
                        }
                    }
                }
            }
        }
    }
}