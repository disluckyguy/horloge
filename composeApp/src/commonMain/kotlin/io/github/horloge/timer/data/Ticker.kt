package io.github.horloge.timer.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class Ticker {
    private val pause = MutableStateFlow(false)
    fun tick(ticks: Int) = flow {
        var i = ticks
        while (i >= 0) {

            delay(1000)

            if (pause.value) {
                emit(i)
                continue
            }
            i -= 1
            emit(i)

        }
    }.cancellable()

    fun pause() = pause.update { true }
    fun resume() = pause.update { false }

}