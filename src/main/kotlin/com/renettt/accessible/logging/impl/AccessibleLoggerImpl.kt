package com.renettt.accessible.logging.impl

import com.renettt.accessible.logging.AccessibleLogger
import java.text.SimpleDateFormat
import java.util.*

class AccessibleLoggerImpl : AccessibleLogger {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    override fun log(message: String, level: AccessibleLogger.LoggerLever) {
        val currentDate = Date(System.currentTimeMillis())
        println("$level  |  ${dateFormatter.format(currentDate)}  |  $message")
    }
}
