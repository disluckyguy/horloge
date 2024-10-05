package io.github.clock

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class Date(val time: String, val pm: Boolean, val date: String)

data class WorldClock(val continent: String, val city: String, var date: Date)

fun getCurrentDate(): Date {
    val zone = TimeZone.currentSystemDefault();
    val now = Clock.System.now().toLocalDateTime(zone);

    val daySliced =
        now.dayOfWeek.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    val monthSliced =
        now.month.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    val hours = if (now.hour >= 13) now.hour - 12 else now.hour
    //val hours = now.hour

    val pm = now.hour >= 12
    return Date(
        "$hours:${if (now.minute < 10) "0" + now.minute.toString() else now.minute}",
        pm,
        "$daySliced, $monthSliced ${now.dayOfMonth}"
    )
}

fun getDateForWorldClock(worldClock: WorldClock): Date {
    val zone = TimeZone.of("${worldClock.continent}/${worldClock.city}");
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(zone);

    val daySliced =
        localTime.dayOfWeek.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    val monthSliced =
        localTime.month.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    val hours = if (localTime.hour >= 13) localTime.hour - 12 else localTime.hour
    //val hours = localTime.hour
    
    val pm = localTime.hour >= 12
    return Date(
        "$hours:${if (localTime.minute < 10) "0" + localTime.minute.toString() else localTime.minute}",
        pm,
        "$daySliced, $monthSliced ${localTime.dayOfMonth}"
    )


}

fun getDateForTimeZone(timeZone: TimeZone): Date {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(timeZone);

    val daySliced =
        localTime.dayOfWeek.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    val monthSliced =
        localTime.month.name.slice(0..2).lowercase().replaceFirstChar { c -> c.uppercase() };
    //val hours = if (now.hour >= 13) now.hour - 12 else now.hour
    val hours = if (localTime.hour >= 13) localTime.hour - 12 else localTime.hour

    val pm = localTime.hour >= 12
    return Date(
        "$hours:${if (localTime.minute < 10) "0" + localTime.minute.toString() else localTime.minute}",
        pm,
        "$daySliced, $monthSliced ${localTime.dayOfMonth}"
    )


}

@Composable
fun ClockPage(modifier: Modifier = Modifier) {
    var date by remember { mutableStateOf(getCurrentDate()) }

    val worldClocks = remember { mutableStateListOf<WorldClock>() }

    worldClocks.add(WorldClock("Africa", "Cairo", getDateForTimeZone(TimeZone.of("Africa/Cairo"))))
    worldClocks.add(WorldClock("Africa", "Cairo", getDateForTimeZone(TimeZone.of("Africa/Cairo"))))


    Box(Modifier.fillMaxSize()) {

        LazyColumn {
            item {
                Box(Modifier.padding(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        LaunchedEffect(true) { // Restart the effect when the pulse rate changes
                            while (true) {
                                delay(1000) // Pulse the alpha every pulseRateMs to alert the user
                                date = getCurrentDate()
                            }
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(date.time, style = TextStyle(fontSize = 64.sp))
                            Text(
                                if (date.pm) "PM" else "AM",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }



                        Text(
                            date.date,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            for ((index, clock) in worldClocks.withIndex()) {
                item {
                    Box(Modifier.padding(8.dp)) {
                        Card {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                            ) {

                                LaunchedEffect(true) { // Restart the effect when the pulse rate changes
                                    while (true) {
                                        delay(1000)
                                        val updatedClock = clock.copy(date = getDateForWorldClock(clock))
                                        // Pulse the alpha every pulseRateMs to alert the user
                                        worldClocks[index] = updatedClock
                                    }
                                }

                                Text(
                                    text = "${clock.date.time} ${if (clock.date.pm) "PM" else "AM"}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${clock.city}, ${clock.continent}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(Modifier.padding(12.dp).align(Alignment.BottomCenter)) {
            LargeFloatingActionButton(
                onClick = {},
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add World Clock"
                )
            }
        }
    }


}
