package com.renettt.accessible.checks

/**
 * Contains basic information about check.
 * checkId - id mapping of check in the system,
 * description - description about the check,
 * link - absolute link to documentations about this type of check
 */
data class AccessibilityCheckMetaData(
    val checkId: String,
    val description: String,
    val link: String
)
