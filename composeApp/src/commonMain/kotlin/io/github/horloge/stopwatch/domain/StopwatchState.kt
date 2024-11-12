package io.github.horloge.stopwatch.domain


sealed class StopwatchState(
    val millis: Long,
    val ranks: List<Long>,
)

class StopwatchInitial : StopwatchState(0, listOf())

class StopwatchPaused(millis: Long, ranks: List<Long>) : StopwatchState(millis, ranks)

class StopwatchInProgress(millis: Long, ranks: List<Long>) : StopwatchState(millis, ranks)
