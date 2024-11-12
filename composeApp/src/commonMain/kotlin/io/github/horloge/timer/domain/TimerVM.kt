package io.github.horloge.timer.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import io.github.horloge.timer.data.Ticker
import io.github.horloge.timer.data.Timer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TimerState>(TimerCreateEmpty())
    val uiState = _uiState.asStateFlow()
    private val ticker = Ticker()

    private var tickJob: Job? = null

    fun update(event: TimerEvent) {

        when (event) {
            is TimerPause -> onPause(event)
            is TimerReset -> onReset(event)
            is TimerResume -> onResume(event)
            is TimerStarted -> onStarted(event)
            is TimerTicked -> onTicked(event)
            is HoursAdded -> onHoursAdded(event)
            is MinutesAdded -> onMinutesAdded(event)
            is SecondsAdded -> onSecondsAdded(event)
        }
    }

    private fun onStarted(event: TimerStarted) {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
                ticker.tick(event.timer.toSeconds()).collect {
                update(TimerTicked(it))
            }
        }
        _uiState.update { TimerInProgress(event.timer) }
    }

    private fun onPause(@Suppress("UNUSED_PARAMETER") event: TimerPause) {
        if (_uiState.value is TimerInProgress) {
            ticker.pause()
            _uiState.update {
                TimerPaused(uiState.value.timer)
            }
        }
    }

    private fun onResume(@Suppress("UNUSED_PARAMETER") event: TimerResume) {
        if (_uiState.value is TimerPaused) {
            ticker.resume()
            _uiState.update {
                TimerInProgress(it.timer)
            }
        }
    }

    private fun onReset(@Suppress("UNUSED_PARAMETER") event: TimerReset) {
        tickJob?.cancel()
        _uiState.update {
            TimerCreateReady(uiState.value.timer.reset())
        }
    }

    private fun onTicked(event: TimerTicked) {
        if (event.duration == uiState.value.timer.toSeconds()) {
            return
        }
        _uiState.update {
            if (event.duration > 0) {
                TimerInProgress(Timer.fromSeconds(event.duration, uiState.value.timer.original))
            } else {
                TimerCreateReady(_uiState.value.timer.reset())
            }
        }

    }

    private fun onSecondsAdded(event: SecondsAdded) {

        val timer = _uiState.value.timer
        val newSeconds = addNumberToTime(timer.seconds, event.number)
        val newTimer = _uiState.value.timer.copy(seconds = newSeconds, original = timer.hours * 3600 + timer.minutes * 60 + newSeconds)
        _uiState.update {
            if (newTimer.toSeconds() == 0) {
                TimerCreateEmpty()
            } else {
                TimerCreateReady(newTimer)
            }
        }
    }

    private fun onMinutesAdded(event: MinutesAdded) {
        val timer = _uiState.value.timer
        val newMinutes = addNumberToTime(timer.minutes, event.number)
        val newTimer = _uiState.value.timer.copy(minutes = newMinutes, original = timer.hours * 3600 + newMinutes * 60 + timer.seconds)
        _uiState.update {
            if (newTimer.toSeconds() == 0) {
                TimerCreateEmpty()
            } else {
                TimerCreateReady(newTimer)
            }
        }
    }

    private fun onHoursAdded(event: HoursAdded) {

        val timer = _uiState.value.timer
        val newHours = addNumberToTime(timer.hours, event.number)

        val newTimer = _uiState.value.timer.copy(hours = newHours, original = newHours * 3600 + timer.minutes * 60 + timer.seconds)
        _uiState.update {
            if (newTimer.toSeconds() == 0) {
                TimerCreateEmpty()
            } else {
                TimerCreateReady(newTimer)
            }
        }
    }

    private fun addNumberToTime(number: Int, added: Int): Int {
        return when (number) {
            0 -> {
                if (added == -1) {
                    59
                } else {
                    number + added
                }
            }
            59 -> {
                if (added == 1) {
                    0
                } else {
                    number + added
                }
            }
            else -> {
                number + added
            }
        }
    }
}

