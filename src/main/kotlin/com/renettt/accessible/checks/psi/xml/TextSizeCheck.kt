package com.renettt.accessible.checks.psi.xml

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.XmlAccessibilityCheck
import java.util.*

class TextSizeCheck : XmlAccessibilityCheck {
    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "Text size check",
            description = "Проверка возможности адаптации размера текста с системными настройками шрифтов",
            link = "https://support.google.com/accessibility/android/answer/12159181"
        )
    }

    override fun availableFor(element: XmlElement): Boolean {
        return element is XmlTag && element.isValid && checkIfContainsTextSize(element)
    }

    private fun checkIfContainsTextSize(tag: XmlTag): Boolean {
        return findTextSizeAttribute(tag) != null
    }

    private fun findTextSizeAttribute(tag: XmlTag): XmlAttribute? {
        for (attribute in tag.attributes) {
            if (attribute.name == TEXT_SIZE_ATTR)
                return attribute
        }
        return null
    }

    override fun performCheckOperations(element: XmlElement): List<AccessibilityCheckResult> {
        element as XmlTag

        val textSizeAttribute = findTextSizeAttribute(element)
        return if (textSizeAttribute == null)
            emptyList()
        else {
            val results = mutableListOf<AccessibilityCheckResult>()

            val attributeValue = textSizeAttribute.value
            if (attributeValue.isNullOrBlank()) {
                results.add(
                    AccessibilityCheckResult(
                        type = AccessibilityCheckResultType.ERROR,
                        metadata = null,
                        msg = "Invalid value for `$TEXT_SIZE_ATTR`"
                    )
                )
            } else {
                if (attributeValue.contains(TEXT_SIZE_ATTR_VALUE_POSTFIX_DP))
                    results.add(
                        AccessibilityCheckResult(
                            type = AccessibilityCheckResultType.ERROR,
                            metadata = null,
                            msg = "Attribute `${TEXT_SIZE_ATTR}` must be in scale-independent pixels ($TEXT_SIZE_ATTR_VALUE_POSTFIX_SP)"
                        )
                    )
            }

            return results
        }
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }

    companion object {
        // https://developer.android.com/reference/android/widget/TextView#attr_android:textSize
        private const val TEXT_SIZE_ATTR = "android:textSize"

        private const val TEXT_SIZE_ATTR_VALUE_POSTFIX_DP = "dp"
        private const val TEXT_SIZE_ATTR_VALUE_POSTFIX_SP = "sp"
    }
}
