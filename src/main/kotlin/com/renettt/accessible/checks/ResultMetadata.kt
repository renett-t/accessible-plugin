package com.renettt.accessible.checks

interface ResultMetadata {

    fun getStringMessage(): String

    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getInt(key: String): Int
    fun getDouble(key: String): Double

    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)
    fun putInt(key: String, value: Int)
    fun putDouble(key: String, value: Double)
}
