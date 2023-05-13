package com.renettt.accessible.settings

data class AccessibleState(
    val minTouchTargetSize: Int = 48,
    var minTouchTargetSizeOverrideForAll: Int = 48,
    var minTouchTargetSizeOverride: Map<String, MutableList<String>> = mapOf("48dp" to mutableListOf(""))
)
