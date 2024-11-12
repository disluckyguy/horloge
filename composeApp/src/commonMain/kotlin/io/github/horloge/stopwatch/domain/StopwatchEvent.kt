package io.github.horloge.stopwatch.domain

sealed class StopwatchEvent

data object RankAdded : StopwatchEvent()

data object StopwatchStart : StopwatchEvent()

data object StopwatchPause : StopwatchEvent()

data object StopwatchResume : StopwatchEvent()

data object StopwatchReset : StopwatchEvent()

data class StopwatchTicked(val millis: Long) : StopwatchEvent()

