package com.renettt.accessible.checks.psi.xml

import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.XmlAccessibilityCheck
import java.util.*

class ClickTargetSizeCheck : XmlAccessibilityCheck {

    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "click-target-size",
            description = "Проверка размера области клика",
            link = "https://support.google.com/accessibility/android/answer/7101858"
        )
    }

    override fun runCheck(element: XmlElement): List<AccessibilityCheckResult> {
        val isValid = element.isValid
        val firstChild = element.firstChild
        val lastChild = element.lastChild
        val parent = element.parent

        val navigationElement = element.navigationElement
        val originalElement = element.originalElement

        val isXmlTag = element is XmlTag
        if (isXmlTag) {
            element as XmlTag

            val attributes = element.attributes
            for (attr in attributes) {
                val displayValue = attr.displayValue
                val value = attr.value
                val name = attr.name
            }
            val subTags = element.subTags
            val name = element.name
            val localName = element.localName

        }

        return emptyList()
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }
}
