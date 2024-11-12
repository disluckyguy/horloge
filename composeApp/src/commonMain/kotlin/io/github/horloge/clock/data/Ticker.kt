package io.github.horloge.clock.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class Ticker {
    private val pause = MutableStateFlow(false)
    fun tick() = flow {
        while (true) {
            delay(1000)

            emit(true)

        }
    }.cancellable()

    fun pause() = pause.update { true }
    fun resume() = pause.update { false }

}