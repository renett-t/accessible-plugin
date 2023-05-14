package com.renettt.accessible.checks

/**
 * Loads all available checks into service
 */
interface AccessibilityChecksLoader<Element> {

    fun load(): List<AccessibilityCheck<Element>>

}
