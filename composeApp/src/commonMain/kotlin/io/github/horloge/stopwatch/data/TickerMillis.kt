package io.github.horloge.stopwatch.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class TickerMillis {
    private val pause = MutableStateFlow(false)
    fun tick() = flow {
        var i: Long = 0
        while (true) {

            delay(10)

            if (pause.value) {
                emit(i)
                continue
            }
            i += 10L
            emit(i)
        }
    }.cancellable()

    fun pause() = pause.update { true }
    fun resume() = pause.update { false }

}