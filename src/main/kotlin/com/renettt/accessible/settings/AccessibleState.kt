package com.renettt.accessible.settings

data class AccessibleState(
    var minTouchTargetSize: String = "48dp",
    var minTouchTargetSizeOverride: Map<String, MutableList<String>> = mapOf("48dp" to mutableListOf(""))
)
