package com.renettt.accessible.checks.psi.xml

import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.XmlAccessibilityCheck
import java.util.*

class ContentDescriptionCheck : XmlAccessibilityCheck {

    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "content-description",
            description = "Проверка наличия speakable текста",
            link = "https://support.google.com/accessibility/android/answer/7158690?hl=en"
        )
    }

    override fun availableFor(element: XmlElement): Boolean {
        return element is XmlTag && element.isValid && checkIfGraphical(element)
    }

    override fun performCheckOperations(element: XmlElement): List<AccessibilityCheckResult> {
        element as XmlTag
        val checkResults: List<AccessibilityCheckResult> = checkTagContentDescription(element)

        val tagName = element.name
        val tagAttrs = element.attributes
        val debugBreakPointValue = 26

        return checkResults
    }

    private fun checkTagContentDescription(tag: XmlTag): List<AccessibilityCheckResult> {
        val checkResults = mutableListOf<AccessibilityCheckResult>()

        var containsContentDescription = false
        var suppressed = false

        for (attribute in tag.attributes) {
            if (attribute.name == IMPORTANT_FOR_ACCESSIBILITY_ATTR) {
                suppressed = true
                break
            }

            if (attribute.name == CONTENT_DESCRIPTION_ATTR) {
                val attrValue = attribute.value
                if (IMPORTANT_FOR_ACCESSIBILITY_ATTR_IGNORE_VALUES.contains(attrValue)
                    || attrValue == CONTENT_DESCRIPTION_VALUE_NULL) {
                    suppressed = true
                    break
                } else if (attrValue == "null") {
                    checkResults.add(
                        AccessibilityCheckResult(
                            type = AccessibilityCheckResultType.ERROR,
                            metadata = null,
                            msg = "Incorrect value for `${attribute.name}`. Should be not null or suppressed if needed"
                        )
                    )
                } else {
                    containsContentDescription = true
                }
            }
        }

        return if (suppressed)
            emptyList()
        else if (!containsContentDescription) {
            checkResults.add(
                AccessibilityCheckResult(
                    type = AccessibilityCheckResultType.ERROR,
                    metadata = null,
                    msg = "Missing `$CONTENT_DESCRIPTION_ATTR`. Provide text for TalkBack"
                )
            )

            checkResults
        } else
            emptyList()
    }

    private fun checkIfGraphical(tag: XmlTag): Boolean {
        return TAGS_TO_CHECK.contains(tag.name.toLowerCase())
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }


    companion object {
        // yes i'm aware that это хардкод и если назвать вьюшку как image-что-то но она не будет содержать image
        // вернётся false positive результат
        private val TAGS_TO_CHECK = listOf("image", "imagebutton", "imageview", "checkbox")

        // https://developer.android.com/reference/android/view/View.html#attr_android:importantForAccessibility
        private const val IMPORTANT_FOR_ACCESSIBILITY_ATTR = "android:importantForAccessibility"
        private val IMPORTANT_FOR_ACCESSIBILITY_ATTR_IGNORE_VALUES = listOf("noHideDescendants", "no")

        // https://developer.android.com/reference/android/view/View.html#attr_android:contentDescription
        private const val CONTENT_DESCRIPTION_ATTR = "android:contentDescription"
        private const val CONTENT_DESCRIPTION_VALUE_NULL = "@null"
    }
}
