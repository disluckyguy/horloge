package io.github.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Date(
    val time: String,
    val pm: Boolean,
    val date: String,
    val continent: String,
    val city: String
)

fun getCurrentDate(): Date {
    val zone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toLocalDateTime(zone)

    val (continent, city) = zone.id.split("/")
    val daySliced =
        now.dayOfWeek.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() }
    val monthSliced =
        now.month.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() }
    val hours = if (now.hour >= 13) now.hour - 12 else now.hour
    //val hours = now.hour

    val pm = now.hour >= 12
    return Date(
        "$hours:${if (now.minute < 10) "0" + now.minute.toString() else now.minute}",
        pm,
        "$daySliced, $monthSliced ${now.dayOfMonth}",
        continent,
        city
    )
}

@Composable
fun ClockPage(windowSizeClass: WindowSizeClass) {
    var date by remember { mutableStateOf(getCurrentDate()) }
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(1) {
        while (true) {
            date = getCurrentDate()
            delay(1000)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
            Canvas(modifier = Modifier) {
                drawCircle(
                    color = if (isDarkTheme) {
                        onSecondaryDark
                    } else onSecondaryLight,
                    radius = 100f,
                    style = Fill,
                )
            }
            Canvas(modifier = Modifier) {
                drawCircle(
                    color = if (isDarkTheme) {
                        outlineDark
                    } else outlineLight,
                    radius = 105f,
                    style = Stroke(width = 5f),
                )
            }
            Text(
                text = date.time,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Text("${date.city}, ${date.continent}", style = MaterialTheme.typography.displaySmall)
    }
}
