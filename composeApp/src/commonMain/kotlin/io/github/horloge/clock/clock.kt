package io.github.horloge.clock

import io.github.horloge.clock.domain.ClockViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clockModule = module {
    viewModel{ ClockViewModel()}
}