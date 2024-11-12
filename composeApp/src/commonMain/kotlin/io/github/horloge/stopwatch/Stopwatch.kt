package io.github.horloge.stopwatch

import io.github.horloge.clock.domain.ClockViewModel
import io.github.horloge.stopwatch.domain.StopwatchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val stopwatchModule = module {
    viewModel{ StopwatchViewModel() }
}