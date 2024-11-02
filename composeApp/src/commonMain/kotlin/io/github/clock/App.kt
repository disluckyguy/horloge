package io.github.clock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.window.core.layout.WindowSizeClass
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    CLOCK("Clock", Icons.Default.Watch, "Show current time and world clocks"),
    STOPWATCH("Stopwatch", Icons.Default.LockClock, "Stopwatch"),
    TIMER("Timer", Icons.Default.HourglassBottom, "Create a countdown with a specific time"),
}

@Composable
@Preview
fun App(windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass) {
    AppTheme {

        var currentDestination by remember { mutableStateOf(AppDestinations.CLOCK) }

        NavigationSuiteScaffold(
            navigationSuiteItems = {

                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = it.contentDescription
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it },
                    )
                }
            },

            ) {
            when (currentDestination) {
                AppDestinations.CLOCK -> ClockPage(windowSizeClass = windowSizeClass)
                AppDestinations.STOPWATCH -> StopwatchPage(windowSizeClass = windowSizeClass)
                AppDestinations.TIMER -> TimerPage(windowSizeClass = windowSizeClass)
            }
        }

    }
}