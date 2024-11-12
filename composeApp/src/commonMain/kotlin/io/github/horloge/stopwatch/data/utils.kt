package io.github.horloge.stopwatch.data

fun formatTime(seconds: Long) : String{

    val newSeconds = (seconds % 3600) % 60L
    val minutes = (newSeconds % 3600) / 60L
    val hours = newSeconds / 3600L
    val hoursStr = hours.toString()
    val minutesStr = if (minutes < 10 || hours != 0L) minutes.toString().padStart(2, '0') else "$minutes"
    val secondsStr =  if (newSeconds < 10 || minutes != 0L) newSeconds.toString().padStart(2, '0') else "$newSeconds"
    val fullStr = "${if (hours > 0) "$hoursStr:" else ""}${if (minutes > 0) "$minutesStr:" else ""}$secondsStr"
    return fullStr
}