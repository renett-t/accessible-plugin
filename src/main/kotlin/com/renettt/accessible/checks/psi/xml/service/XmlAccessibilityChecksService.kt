package com.renettt.accessible.checks.psi.xml.service

import com.intellij.psi.xml.XmlElement
import com.renettt.accessible.checks.psi.PsiAccessibilityChecksService

/**
 * Сервис по проверке xml-элементов
 */
class XmlAccessibilityChecksService(
    checksLoader: XmlAccessibilityChecksLoader
) : PsiAccessibilityChecksService<XmlElement>(checksLoader)
