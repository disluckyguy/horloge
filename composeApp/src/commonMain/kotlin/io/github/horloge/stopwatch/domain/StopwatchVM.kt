package io.github.horloge.stopwatch.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.horloge.stopwatch.data.TickerMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<StopwatchState>(StopwatchInitial())
    val uiState = _uiState.asStateFlow()
    private val ticker = TickerMillis()

    private var tickJob: Job? = null


    fun update(event: StopwatchEvent) {
        when (event) {
            is RankAdded -> onRankAdded(event)
            is StopwatchPause -> onPause(event)
            is StopwatchReset -> onReset(event)
            is StopwatchResume -> onResume(event)
            is StopwatchTicked -> onTicked(event)
            is StopwatchStart -> onStarted(event)
        }
    }

    private fun onStarted(event: StopwatchStart) {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            ticker.tick().collect {
                update(StopwatchTicked(it))
            }
        }
        _uiState.update { StopwatchInProgress(0, listOf()) }
    }

    private fun onRankAdded(event: RankAdded) {
        _uiState.update {
            StopwatchInProgress(it.millis, it.ranks + it.millis)
        }
    }

    private fun onPause(@Suppress("UNUSED_PARAMETER") event: StopwatchPause) {
        if (_uiState.value is StopwatchInProgress) {
            ticker.pause()
            _uiState.update {
                StopwatchPaused(_uiState.value.millis, _uiState.value.ranks)
            }
        }
    }

    private fun onResume(@Suppress("UNUSED_PARAMETER") event: StopwatchResume) {
        if (_uiState.value is StopwatchPaused) {
            ticker.resume()
            _uiState.update {
                StopwatchInProgress(_uiState.value.millis, _uiState.value.ranks)
            }
        }
    }

    private fun onReset(@Suppress("UNUSED_PARAMETER") event: StopwatchReset) {
        tickJob?.cancel()
        _uiState.update {
            StopwatchInitial()
        }
    }

    private fun onTicked(event: StopwatchTicked) {
        if (event.millis == uiState.value.millis) {
            return
        }
        _uiState.update {
            StopwatchInProgress(event.millis, _uiState.value.ranks)
        }

    }


}

