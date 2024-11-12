package io.github.horloge.stopwatch.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import io.github.horloge.stopwatch.data.formatTime
import io.github.horloge.stopwatch.domain.RankAdded
import io.github.horloge.stopwatch.domain.StopwatchInitial
import io.github.horloge.stopwatch.domain.StopwatchPause
import io.github.horloge.stopwatch.domain.StopwatchPaused
import io.github.horloge.stopwatch.domain.StopwatchReset
import io.github.horloge.stopwatch.domain.StopwatchResume
import io.github.horloge.stopwatch.domain.StopwatchStart
import io.github.horloge.stopwatch.domain.StopwatchViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StopwatchPage(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    viewModel: StopwatchViewModel = koinViewModel()
) {
    val wideMode = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    val state = viewModel.uiState.collectAsState()
    if (wideMode) {

        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

            StopwatchText(millis = state.value.millis)

            rankingsList(modifier = Modifier.weight(3f), ranks = state.value.ranks)

            StopwatchControlsWide(
                viewModel
            )
        }
    } else {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            StopwatchText(millis = state.value.millis)

            rankingsList(modifier = Modifier.weight(3f), ranks = state.value.ranks)

            StopwatchControlsCompact(
                viewModel
            )
        }
    }
}

@Composable
fun StopwatchText(modifier: Modifier = Modifier, millis: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)){
        Text(
            text = formatTime(millis / 1000),
            textAlign = TextAlign.Center,
            style = typography.displayLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = (((millis / 1000f) - millis / 1000f.toInt()) * 1000).toString().substring(0, 2)
                .removeSuffix("."),
            textAlign = TextAlign.Center,
            style = typography.displaySmall,
            color = Color.LightGray,
            modifier = Modifier
        )
    }
}

@Composable
fun rankingsList(modifier: Modifier = Modifier, ranks: List<Long>) {
    LazyColumn(
        modifier
            .fadingEdge(Brush.verticalGradient(0.7f to Color.Red, 1f to Color.Transparent))
    ) {
        for ((i, ranking) in ranks.withIndex()) {
            item {
                Text(
                    "#${i + 1} ${formatTime(ranking / 1000)}.${(((ranking / 1000f) - ranking / 1000f.toInt()) * 1000).toString().substring(0, 2)}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StopwatchControlsWide(
    viewModel: StopwatchViewModel
) {


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StopwatchControlsContent(viewModel = viewModel)
    }
}

@Composable
fun StopwatchControlsCompact(
    viewModel: StopwatchViewModel
) {

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StopwatchControlsContent(viewModel = viewModel)
    }
}

@Composable
fun StopwatchControlsContent(modifier: Modifier = Modifier, viewModel: StopwatchViewModel) {
    val state = viewModel.uiState.collectAsState()

    when (state.value) {

        is StopwatchPaused -> {
            FilledTonalIconButton(
                onClick = {
                    viewModel.update(StopwatchReset)
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
                    viewModel.update(StopwatchResume)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(80.dp)
            ) {
                Icon(
                    Icons.Default.Pause,
                    contentDescription = "Pause"
                )
            }

            FilledTonalIconButton(
                onClick = {
                    viewModel.update(RankAdded)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(50.dp),
                enabled = false
            ) {
                Icon(
                    Icons.Rounded.Timer,
                    contentDescription = "Add Ranking"
                )
            }
        }

        is StopwatchInitial -> {
            FilledTonalIconButton(
                onClick = {
                    viewModel.update(StopwatchReset)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(50.dp),
                enabled = false
            ) {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = "Restart Stopwatch"
                )
            }
            LargeFloatingActionButton(
                onClick = {
                    viewModel.update(StopwatchStart)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(80.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play"
                )
            }

            FilledTonalIconButton(
                onClick = {
                    viewModel.update(RankAdded)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(50.dp),
                enabled = false
            ) {
                Icon(
                    Icons.Rounded.Timer,
                    contentDescription = "Add Ranking"
                )
            }
        }

        else -> {
            FilledTonalIconButton(
                onClick = {
                    viewModel.update(StopwatchReset)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(50.dp),
            ) {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = "Restart Stopwatch"
                )
            }
            LargeFloatingActionButton(
                onClick = {
                    viewModel.update(StopwatchPause)
                },
                shape = CircleShape,
                modifier = Modifier.requiredSize(80.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play"
                )
            }

            FilledTonalIconButton(
                onClick = {
                    viewModel.update(RankAdded)
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

}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }