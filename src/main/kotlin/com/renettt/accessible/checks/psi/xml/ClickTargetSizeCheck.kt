package com.renettt.accessible.checks.psi.xml

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.XmlAccessibilityCheck
import com.renettt.accessible.configure.Configuration
import java.util.*

class ClickTargetSizeCheck : XmlAccessibilityCheck {

    private val state by lazy {
        Configuration().accessibleState(Configuration().project)
    }

    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "click-target-size",
            description = "Проверка размера области клика",
            link = "https://support.google.com/accessibility/android/answer/7101858"
        )
    }

    override fun availableFor(element: XmlElement): Boolean {
        return element is XmlTag && element.isValid
                && checkIfClickable(element)
    }

    private fun checkIfClickable(tag: XmlTag): Boolean {
        return tagIsClickable(tag) || tagIsButton(tag)
    }

    private fun tagIsClickable(tag: XmlTag): Boolean {
        for (attribute in tag.attributes) {
            if (attribute.name == searchedTagAttribute) {
                return true
            }
        }
        return false
    }

    private fun tagIsButton(tag: XmlTag): Boolean {
        return tag.name.contains(searchedTagPartLowerCase, ignoreCase = true)
    }

    override fun performCheckOperations(element: XmlElement): List<AccessibilityCheckResult> {
        element as XmlTag
        val checkResults = mutableListOf<AccessibilityCheckResult>()

        checkResults.addAll(
            checkTagIfButton(element)
        )
        checkResults.addAll(
            checkTagIfClickable(element)
        )

        val tagName = element.name
        val tagAttrs = element.attributes
        val debugBreakPointValue = 25

        return checkResults
    }

    private fun checkTagIfButton(tag: XmlTag): List<AccessibilityCheckResult> {
        val results = mutableListOf<AccessibilityCheckResult>()

        return if (tagIsButton(tag)) {
            results.addAll(checkWidthAndHeight(tag))
            results
        } else
            emptyList()
    }

    private fun checkAttribute(
        tag: XmlTag,
        attribute: XmlAttribute,
        searchedTagName: String,
        fromParent: Boolean = false
    ): AccessibilityCheckResult? {

        if (attribute.name == searchedTagName) {
            val value = attribute.value

            if (value == "match_parent") {
                return tryFindInParent(tag, attribute, searchedTagName)
            } else {
                val parsedValue = value?.subSequence(0, value.length - 2)
                val parsedValueAsInt = parsedValue?.toString()?.toIntOrNull()
                    ?: return null

                return if (parsedValueAsInt < state.minTouchTargetSizeOverrideForAll) {
                    AccessibilityCheckResult(
                        type = AccessibilityCheckResultType.WARNING,
                        metadata = null,
                        msg = "Incorrect value for `${attribute.name}`. Should be more than ${state.minTouchTargetSize}dp. Current = $value",
                    )
                } else
                    null
            }
        } else
            return null
    }

    private fun tryFindInParent(
        tag: XmlTag,
        attribute: XmlAttribute,
        searchedTagName: String
    ): AccessibilityCheckResult? {

        // fixme: searching in parent's parent tags
        val parentTag = tag.parentTag ?: return AccessibilityCheckResult(
            type = AccessibilityCheckResultType.ERROR,
            metadata = null,
            msg = "No data about view's size. Specify attributes (`layout_width`, `layout_height`)"
        )

        for (parentAttribute in parentTag.attributes) {
            if (parentAttribute.name == searchedTagName)
                return checkAttribute(parentTag, parentAttribute, searchedTagName, fromParent = true)
        }

        return null
    }

    private fun checkTagIfClickable(tag: XmlTag): Collection<AccessibilityCheckResult> {
        val results = mutableListOf<AccessibilityCheckResult>()

        if (tagIsClickable(tag)) {
            results.addAll(checkWidthAndHeight(tag))
        }

        return results
    }

    private fun checkWidthAndHeight(tag: XmlTag): Collection<AccessibilityCheckResult> {
        val results = mutableListOf<AccessibilityCheckResult>()

        for (attribute in tag.attributes) {
            val res1: AccessibilityCheckResult? = checkAttribute(tag, attribute, searchedAttributeHeight)
            if (res1 != null) {
                results.add(res1)
            }

            val res2: AccessibilityCheckResult? = checkAttribute(tag, attribute, searchedAttributeWidth)
            if (res2 != null) {
                results.add(res2)
            }
        }

        return results
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }

    companion object {
        // по какому признаку тэг можно проверять этим классом
        private const val searchedTagPartLowerCase = "button"
        private const val searchedTagAttribute = "android:clickable"

        // какие признаки проверять
        private const val searchedAttributeWidth = "android:layout_width"
        private const val searchedAttributeHeight = "android:layout_height"
    }

}
