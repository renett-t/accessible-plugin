package com.renettt.accessible.settings

data class AccessibleState(
    var minTouchTargetSize: String = "48",
    var minTouchTargetSizeOverrideForAll: String = "48",
    var minTouchTargetSizeOverride: Map<String, MutableList<String>> = mapOf("48dp" to mutableListOf(""))
)
