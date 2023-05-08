package com.renettt.accessible.checks.psi

import com.intellij.psi.xml.XmlElement
import com.renettt.accessible.checks.AccessibilityCheck

/**
 * Base interface for all accessibility checks performed on XmlElement (xml files).
 * Help documentation on xml psi - https://github.com/JetBrains/intellij-community/blob/master/xml/xml-psi-api/src/com/intellij/psi/xml/XmlTag.java
 */
interface XmlAccessibilityCheck : AccessibilityCheck<XmlElement>
