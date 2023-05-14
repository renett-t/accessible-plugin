package com.renettt.accessible.checks.psi.xml.service

import com.intellij.psi.xml.XmlElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityChecksLoader
import com.renettt.accessible.checks.psi.xml.ClickTargetSizeCheck

class XmlAccessibilityChecksLoader : AccessibilityChecksLoader<XmlElement> {

    override fun load(): List<AccessibilityCheck<XmlElement>> {
        return listOf(
            ClickTargetSizeCheck()
        )
    }
}
