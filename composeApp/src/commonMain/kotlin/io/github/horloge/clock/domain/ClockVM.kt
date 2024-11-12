package io.github.horloge.clock.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.horloge.clock.data.Ticker
import io.github.horloge.stopwatch.domain.StopwatchInitial
import io.github.horloge.stopwatch.domain.StopwatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class ClockViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClockState())
    val uiState = _uiState.asStateFlow()
    private val ticker = Ticker()

    init {
        viewModelScope.launch {
            ticker.tick().cancellable().collect {
                update()
            }
        }
    }

    private fun update() {

        _uiState.update { ClockState() }
    }


}

