package com.renettt.accessible.logging

interface AccessibleLogger {
    fun log(message: String, level: LoggerLever = LoggerLever.INFO)

    enum class LoggerLever {
        INFO,
        DEBUG,
        WARN,
        ERROR
    }
}
