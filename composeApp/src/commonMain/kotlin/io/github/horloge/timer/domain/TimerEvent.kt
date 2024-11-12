package io.github.horloge.timer.domain

import io.github.horloge.timer.data.Timer

sealed class TimerEvent

data class TimerTicked(val duration: Int) : TimerEvent()

data object TimerPause : TimerEvent()

data object TimerResume : TimerEvent()

data object TimerReset : TimerEvent()

data class TimerStarted(val timer: Timer) : TimerEvent()

data class SecondsAdded(val number: Int) : TimerEvent()

data class HoursAdded(val number: Int) : TimerEvent()

data class MinutesAdded(val number: Int) : TimerEvent()