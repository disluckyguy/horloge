package io.github.clock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.delay

enum class TimerScreen(val title: String) {
    Create("create"),
    Timers("timers")
}

fun addNumberToTime(oldTime: String, added: Int): String {
    val stripped = oldTime.replace(":", "")

    val numbers = stripped.toInt()

    if (numbers.toString().length >= 6) {
        return (oldTime)
    }


    val newNumber = numbers * 10 + added

    var timeString = newNumber.toString()

    while (timeString.length < 6) {
        timeString = "0$timeString"
    }

    val time =
        timeString.substring(0..1) + ":" + timeString.substring(2..3) + ":" + timeString.substring(4..5)

    return time
}

fun formatTime(millis: Long): String {
    val secondsFromMillis: Int = (millis / 1000).toInt()
    val hours = secondsFromMillis / 3600
    val minutes = (secondsFromMillis % 3600) / 60
    val seconds = secondsFromMillis % 60

    val timeString = StringBuilder()

    if (hours > 0) {
        timeString.append("$hours:")
    }
    val minuteString = if (minutes < 10 && hours > 0) "0$minutes" else minutes.toString()
    val secondString = if (seconds < 10 && minutes > 0) "0$seconds" else seconds.toString()
    if (hours > 0) {
        timeString.append("${minuteString}:")
    }
    timeString.append(secondString)

    return timeString.toString()
}

fun formatTimeMillis(millis: Long): String {

    val secondsFromMillis: Int = (millis / 1000).toInt()
    val hours = secondsFromMillis / 3600
    val minutes = (secondsFromMillis % 3600) / 60
    val seconds = secondsFromMillis % 60
    val millisFromSeconds = millis % 1000

    val timeString = StringBuilder()

    if (hours > 0) {
        timeString.append("$hours:")
    }
    val minuteString = if (minutes < 10 && hours > 0) "0$minutes" else minutes.toString()
    val secondString = if (seconds < 10 && minutes > 0) "0$seconds" else seconds.toString()
    if (hours > 0) {
        timeString.append("${minuteString}:")
    }
    timeString.append(secondString)
    timeString.append(".${millisFromSeconds / 10}")

    return timeString.toString()
}

fun stringToMilliseconds(timeString: String): Long {
    val parts = timeString.split(":")

    if (parts.size != 3) {
        throw IllegalArgumentException("Invalid time format: $timeString")
    }

    val hours = parts[0].toLong()
    val minutes = parts[1].toLong()
    val seconds = parts[2].toLong()

    return (hours * 3600 + minutes * 60 + seconds) * 1000

}


fun removeNumberFromTime(time: String): String {

    var numbersString = time

    for (number in numbersString.withIndex()) {
        if (number.value != '0' && number.value != ':') {
            numbersString = numbersString.replaceFirst(number.value.toString(), "0")

            return numbersString
        }
    }

    return time
}

@Composable
fun TimerPage(windowSizeClass: WindowSizeClass) {
    val timers = remember { mutableStateListOf<Long>() }
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TimerScreen.Create.title,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(TimerScreen.Create.title) { CreateTimer(navController, timers, windowSizeClass) }
        composable(TimerScreen.Timers.title) {
            TimersList(navController = navController, timers = timers)
        }
    }
}

@Composable
fun CreateTimer(
    navController: NavController,
    timers: MutableList<Long>,
    windowSizeClass: WindowSizeClass
) {
    var time by remember { mutableStateOf("00:00:00") }
    var showStartButton by remember { mutableStateOf(true) }
    val wideMode = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    if (wideMode) {
        CreateTimerWide(
            time = time,
            onTimeChange = { time = it },
            navController = navController,
            showStartButton = showStartButton,
            onStartChange = { showStartButton = it },
            timers = timers,
        )
    } else {
        CreateTimerCompact(
            time = time,
            onTimeChange = { time = it },
            navController = navController,
            showStartButton = showStartButton,
            onStartChange = { showStartButton = it },
            timers = timers,
        )
    }
}

@Composable
fun CreateTimerWide(
    navController: NavController,
    time: String,
    onTimeChange: (String) -> Unit,
    showStartButton: Boolean,
    onStartChange: (Boolean) -> Unit,
    timers: MutableList<Long>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.verticalScroll(state = rememberScrollState())
    ) {

        Text(
            text = time,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp).weight(3f),
            maxLines = 1
        )



        Column(
            Modifier.padding(12.dp).fillMaxWidth().weight(3f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 1))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("1") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 2))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("2") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("3") }
            }
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 4))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("4") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 5))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("5") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 6))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("6") }
            }
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 7))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("7") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 8))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("8") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 9))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("9") }
            }
            Row {
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) {
                    Text("00", maxLines = 1)
                }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("0") }
                Button(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(removeNumberFromTime(time))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Remove"
                    )
                }
            }
        }

        Row(modifier = Modifier.weight(2f)) {
            AnimatedVisibility(
                !showStartButton,
                enter = scaleIn(),
                exit = scaleOut(),

                ) {
                FloatingActionButton(
                    modifier = Modifier.requiredSize(100.dp).padding(10.dp),
                    shape = CircleShape,
                    onClick = {
                        navController.navigate("timers")
                        timers.add(stringToMilliseconds(time))
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

@Composable
fun CreateTimerCompact(
    navController: NavController,
    time: String,
    onTimeChange: (String) -> Unit,
    showStartButton: Boolean,
    onStartChange: (Boolean) -> Unit,
    timers: MutableList<Long>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(state = rememberScrollState())
    ) {

        Text(
            text = time,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp),
            maxLines = 1
        )



        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 1))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("1") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 2))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("2") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("3") }
            }
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 4))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("4") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 5))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("5") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 6))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("6") }
            }
            Row {

                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 7))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("7") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 8))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("8") }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 9))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("9") }
            }
            Row {
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) {
                    Text("00", maxLines = 1)
                }
                FilledTonalButton(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(addNumberToTime(time, 0))
                        onTimeChange(addNumberToTime(time, 0))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) { Text("0") }
                Button(
                    modifier = Modifier.padding(4.dp).requiredSize(60.dp),
                    onClick = {
                        onTimeChange(removeNumberFromTime(time))
                        onStartChange(time == "00:00:00")
                    },
                    shape = CircleShape,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Remove"
                    )
                }
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            AnimatedVisibility(
                !showStartButton,
                enter = scaleIn(),
                exit = scaleOut(),

                ) {
                FloatingActionButton(
                    modifier = Modifier.requiredSize(100.dp).padding(10.dp),
                    shape = CircleShape,
                    onClick = {
                        navController.navigate("timers")
                        timers.add(stringToMilliseconds(time))
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


@Composable
fun TimersList(
    navController: NavController,
    timers: MutableList<Long>
) {
    Box {

        LazyVerticalGrid(
            columns = GridCells.Adaptive(360.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            for (timer in timers) {
                item {
                    Card(modifier = Modifier.height(240.dp).fillMaxWidth().padding(10.dp)) {
                        Box {
                            FilledTonalIconButton(
                                onClick = {

                                    if (timers.size - 1 == 0) {
                                        navController.navigate("create")
                                    }
                                    timers.remove(timer)
                                },
                                shape = CircleShape,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {

                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Localized description"
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val time = remember { mutableStateOf(timer) }
                                var remainingTime by remember { mutableStateOf(time.value) }
                                var paused by remember { mutableStateOf(false) }
                                var currentProgress by remember { mutableStateOf(1f - (remainingTime.toFloat() / time.value)) }


                                LaunchedEffect(key1 = Unit) {
                                    while (remainingTime > 0) {

                                        delay(1000)
                                        if (paused) continue
                                        remainingTime -= 1000
                                        currentProgress =
                                            1f - (remainingTime.toFloat() / time.value)
                                    }
                                }
                                Box {
                                    CircularProgressIndicator(
                                        modifier = Modifier.width(200.dp).height(200.dp)
                                            .align(Alignment.Center).padding(10.dp),
                                        progress = { currentProgress },
                                        strokeWidth = 15.dp
                                    )



                                    Text(
                                        formatTime(remainingTime),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(
                                            Alignment.Center
                                        )
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledIconButton(
                                        onClick = { paused = !paused },
                                        Modifier.padding(15.dp)
                                    ) {
                                        Icon(
                                            if (paused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                                            contentDescription = "Localized description"
                                        )
                                    }

                                    FilledIconButton(onClick = {
                                        remainingTime = time.value
                                    }, Modifier.padding(15.dp)) {
                                        Icon(
                                            Icons.Rounded.Refresh,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        LargeFloatingActionButton(

            onClick = {
                navController.navigate("create")
            },
            Modifier.align(Alignment.BottomEnd),
            shape = CircleShape
        ) {
            Icon(Icons.Sharp.Add, "None")
        }
    }
}