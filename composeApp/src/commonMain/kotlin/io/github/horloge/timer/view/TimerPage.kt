package io.github.horloge.timer.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.horloge.timer.domain.HoursAdded
import io.github.horloge.timer.domain.MinutesAdded
import io.github.horloge.timer.domain.SecondsAdded
import io.github.horloge.timer.domain.TimerCreateEmpty
import io.github.horloge.timer.domain.TimerCreateReady
import io.github.horloge.timer.domain.TimerInProgress
import io.github.horloge.timer.domain.TimerPause
import io.github.horloge.timer.domain.TimerPaused
import io.github.horloge.timer.domain.TimerReset
import io.github.horloge.timer.domain.TimerResume
import io.github.horloge.timer.domain.TimerStarted
import io.github.horloge.timer.domain.TimerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimerRunPage(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: TimerViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsState()

    if (state.value is TimerCreateReady) {
        onBack()
    }

    Scaffold {
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = {
                            state.value.timer.toSeconds()
                                .toFloat() / state.value.timer.original.toFloat()
                        },
                        modifier = Modifier.requiredSize(250.dp).padding(12.dp),
                        strokeWidth = 12.dp,
                    )
                    TimerText(viewModel = viewModel)
                }
                Actions(viewModel = viewModel, onBack = onBack)
            }
        }
    }
}

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val duration = state.value.timer

    println(duration.toTimestamp())
    Text(
        text = duration.toTimestamp(),
        style = MaterialTheme.typography.displayLarge,
    )

}

@Composable
fun Actions(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.uiState.collectAsState()
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        when (state.value) {
            is TimerInProgress -> {
                FloatingActionButton(onClick = {
                    viewModel.update(TimerPause)
                }) {
                    Icon(
                        Icons.Rounded.Pause,
                        contentDescription = "Pause Timer"
                    )
                }
                FloatingActionButton(onClick = {
                    viewModel.update(TimerReset)
                    onBack()
                }) {
                    Icon(
                        Icons.Rounded.RestartAlt,
                        contentDescription = "Reset Timer"
                    )
                }
            }

            is TimerPaused -> {
                FloatingActionButton(onClick = {
                    viewModel.update(TimerResume)
                }) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Resume Timer"
                    )
                }
                FloatingActionButton(onClick = {
                    viewModel.update(TimerReset)
                }) {
                    Icon(
                        Icons.Rounded.RestartAlt,
                        contentDescription = "Reset Timer"
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
fun TimerCreatePage(
    modifier: Modifier,
    onStart: () -> Unit,
    viewModel: TimerViewModel = koinViewModel<TimerViewModel>()
) {
    val state = viewModel.uiState.collectAsState()

    if (!(state.value is TimerCreateEmpty || state.value is TimerCreateReady)) {
        onStart()
    }
    Scaffold(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledTonalIconButton(onClick = { viewModel.update(HoursAdded(1)) }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Increase Hours"
                        )
                    }
                    Text(
                        text = (state.value.timer.original / 3600).toString()
                            .padStart(2, '0'),
                        style = MaterialTheme.typography.displayLarge
                    )

                    FilledTonalIconButton(onClick = { viewModel.update(HoursAdded(-1)) }) {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Decrease Hours"
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledTonalIconButton(onClick = { viewModel.update(MinutesAdded(1)) }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Increase Minutes"
                        )
                    }
                    Text(
                        text = ((state.value.timer.original % 3600) / 60).toString()
                            .padStart(2, '0'),
                        style = MaterialTheme.typography.displayLarge
                    )

                    FilledTonalIconButton(onClick = { viewModel.update(MinutesAdded(-1)) }) {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Decrease Minutes"
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledTonalIconButton(onClick = { viewModel.update(SecondsAdded(1)) }) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Increase Seconds"
                        )
                    }
                    Text(
                        text = ((state.value.timer.original % 3600) % 60).toString()
                            .padStart(2, '0'),
                        style = MaterialTheme.typography.displayLarge
                    )

                    FilledTonalIconButton(onClick = { viewModel.update(SecondsAdded(-1)) }) {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Decrease Seconds"
                        )
                    }
                }
            }

            Row(Modifier.size(120.dp), horizontalArrangement = Arrangement.Center) {
                AnimatedVisibility(
                    state.value is TimerCreateReady,
                    enter = scaleIn(),
                    exit = scaleOut(),

                    ) {
                    FloatingActionButton(
                        modifier = Modifier.requiredSize(100.dp).padding(10.dp),
                        shape = CircleShape,
                        onClick = {
                            viewModel.update(TimerStarted(state.value.timer))
                            onStart()
                        }) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "Start Timer",
                        )
                    }

                }
            }
        }


    }
}