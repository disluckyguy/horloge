package io.github.horloge.timer.data

data class Timer(val seconds: Int, val minutes: Int, val hours: Int, val original: Int) {
    companion object {
        fun fromSeconds(seconds: Int, original: Int) = Timer(
            hours = seconds / 3600,
            minutes = (seconds % 3600) / 60,
            seconds = (seconds % 3600) % 60,
            original = original
        )
    }

    fun toSeconds() = hours * 3600 + minutes * 60 + seconds

    fun reset() = Timer((original % 3600) % 60, (original % 3600) / 60, original / 3600, original)

    fun toTimestamp() : String {
        val hoursStr = hours.toString()
        val minutesStr = if (minutes < 10 || hours != 0) minutes.toString().padStart(2, '0') else "$minutes"
        val secondsStr =  if (seconds < 10 || minutes != 0) seconds.toString().padStart(2, '0') else "$seconds"
        val fullStr = "${if (hours > 0) "$hoursStr:" else ""}${if (minutes > 0) "$minutesStr:" else ""}$secondsStr"
        return fullStr
    }

    fun originalToTimeStamp(): String {
        val seconds = (original % 3600) % 60
        val minutes = (original % 3600) / 60
        val hours = original / 3600
        val hoursStr = hours.toString()
        val minutesStr = if (minutes < 10 || hours != 0) minutes.toString().padStart(2, '0') else "$minutes"
        val secondsStr =  if (seconds < 10 || minutes != 0) seconds.toString().padStart(2, '0') else "$seconds"
        val fullStr = "${if (hours > 0) "$hoursStr:" else ""}${if (minutes > 0) "$minutesStr:" else ""}$secondsStr"
        return fullStr
    }
}