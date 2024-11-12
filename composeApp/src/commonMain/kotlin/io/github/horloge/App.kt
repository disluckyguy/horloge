package io.github.horloge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import clock.composeapp.generated.resources.Res
import clock.composeapp.generated.resources.clock_page
import clock.composeapp.generated.resources.stopwatch_page
import clock.composeapp.generated.resources.timer_create_page
import clock.composeapp.generated.resources.timer_page
import clock.composeapp.generated.resources.timer_run_page
import io.github.horloge.clock.clockModule
import io.github.horloge.clock.domain.ClockViewModel
import io.github.horloge.stopwatch.domain.StopwatchViewModel
import io.github.horloge.stopwatch.stopwatchModule
import io.github.horloge.stopwatch.view.StopwatchPage
import io.github.horloge.theme.AppTheme
import io.github.horloge.timer.domain.TimerViewModel
import io.github.horloge.timer.timerModule
import io.github.horloge.timer.view.TimerCreatePage
import io.github.horloge.timer.view.TimerRunPage
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel


enum class AppDestinations(
    val label: StringResource,
    val icon: ImageVector,
    val contentDescription: String
) {
    CLOCK(Res.string.clock_page, Icons.Default.Schedule, "Show current time and world clocks"),
    STOPWATCH(Res.string.stopwatch_page, Icons.Default.Timer, "Stopwatch"),
    TIMER(
        Res.string.timer_page,
        Icons.Default.HourglassBottom,
        "Create a countdown with a specific time"
    ),
    TIMER_CREATE(
        Res.string.timer_create_page,
        Icons.Default.PlayArrow,
        "Create a countdown with a specific time"
    ),
    TIMER_RUN(
        Res.string.timer_run_page,
        Icons.Default.PlayArrow,
        "Create a countdown with a specific time"
    ),
}

@Composable
@Preview
fun App() {

    KoinApplication(application = { modules(timerModule, clockModule, stopwatchModule) }) {
        val navController: NavHostController = rememberNavController()

        var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.CLOCK) }

        val timerViewModel: TimerViewModel = koinViewModel()
        val clockViewModel: ClockViewModel = koinViewModel()
        val stopwatchViewModel: StopwatchViewModel = koinViewModel()

        AppTheme {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.slice(0..2).forEach {
                        item(
                            icon = {
                                Icon(
                                    it.icon,
                                    contentDescription = it.contentDescription
                                )
                            },
                            label = { Text(stringResource(it.label)) },
                            selected = it == currentDestination,
                            onClick = { navController.navigate(it.name); currentDestination = it },
                        )
                    }
                },
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.CLOCK.name,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(AppDestinations.CLOCK.name) {
                        io.github.horloge.clock.view.ClockPage(
                            viewModel = clockViewModel
                        )
                    }
                    composable(AppDestinations.STOPWATCH.name) { StopwatchPage(viewModel = stopwatchViewModel) }
                    navigation(
                        startDestination = AppDestinations.TIMER_CREATE.name,
                        route = AppDestinations.TIMER.name
                    ) {
                        composable(AppDestinations.TIMER_CREATE.name) {
                            TimerCreatePage(
                                Modifier,
                                onStart = { if (currentDestination == AppDestinations.TIMER) navController.navigate(AppDestinations.TIMER_RUN.name) },
                                timerViewModel
                            )
                        }
                        composable(AppDestinations.TIMER_RUN.name) {
                            TimerRunPage(
                                Modifier,
                                onBack = { if (currentDestination == AppDestinations.TIMER) navController.navigate(AppDestinations.TIMER_CREATE.name) },
                                timerViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
