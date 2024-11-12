package io.github.horloge.clock.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ClockState(
    val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    val localTime: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)
)
