package com.example.smartshopper.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateFormatter(
    locale: Locale = Locale.getDefault(),
    zoneId: ZoneId = ZoneId.systemDefault()
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        .withLocale(locale)
        .withZone(zoneId)

    fun formatTime(timestamp: Instant): String {
        return timeFormatter.format(timestamp)
    }
}

val LocalDateFormatter = staticCompositionLocalOf {
    DateFormatter()
}

@Composable
fun formatTime(timestamp: Instant): String {
    val formatter = LocalDateFormatter.current
    return remember(timestamp, formatter) {
        formatter.formatTime(timestamp)
    }
}
