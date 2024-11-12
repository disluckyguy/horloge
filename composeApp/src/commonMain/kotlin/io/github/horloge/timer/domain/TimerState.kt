package io.github.horloge.timer.domain

import io.github.horloge.timer.data.Timer

sealed class TimerState(val timer: Timer)

class TimerCreateEmpty: TimerState(Timer.fromSeconds(0, 0)) {
    override fun toString(): String {
        val secondsStr = timer.seconds.toString().padStart(2, '0')
        val minutesStr = timer.minutes.toString().padStart(2, '0')
        val hoursStr = timer.hours.toString().padStart(2, '0')
        return "TimerCreateEmpty {$secondsStr:$minutesStr:$hoursStr}}"
    }
}

class TimerCreateReady(timer: Timer) : TimerState(timer) {
    override fun toString(): String {
        val secondsStr = timer.seconds.toString().padStart(2, '0')
        val minutesStr = timer.minutes.toString().padStart(2, '0')
        val hoursStr = timer.hours.toString().padStart(2, '0')
        return "TimerCreateReady {$secondsStr:$minutesStr:$hoursStr}}"
    }
}

class TimerInProgress(timer: Timer) : TimerState(timer) {

    override fun toString(): String {
        val secondsStr = timer.seconds.toString().padStart(2, '0')
        val minutesStr = timer.minutes.toString().padStart(2, '0')
        val hoursStr = timer.hours.toString().padStart(2, '0')
        return "TimerInProgress {$secondsStr:$minutesStr:$hoursStr}}"
    }
}

class TimerPaused(timer: Timer) : TimerState(timer) {

    override fun toString(): String {
        val secondsStr = timer.seconds.toString().padStart(2, '0')
        val minutesStr = timer.minutes.toString().padStart(2, '0')
        val hoursStr = timer.hours.toString().padStart(2, '0')
        return "TimerPaused {$secondsStr:$minutesStr:$hoursStr}}"
    }
}