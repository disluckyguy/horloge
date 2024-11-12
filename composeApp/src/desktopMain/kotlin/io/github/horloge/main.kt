package io.github.horloge

import java.awt.Dimension
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Clock",
    ) {
        window.minimumSize = Dimension(360,294)
        App()
    }
}