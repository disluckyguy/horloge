package io.github.horloge.clock.domain

sealed class TimerEvent

data object ClockTicked : TimerEvent()
