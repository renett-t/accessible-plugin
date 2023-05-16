package com.renettt.accessible.checks.psi.xml

import com.android.ide.common.rendering.api.ResourceNamespace
import com.android.resources.ResourceFolderType
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.XmlAccessibilityCheck
import org.jetbrains.android.facet.AndroidFacet
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.*
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.utils.ContrastRatioCalculator
import org.jetbrains.android.dom.converters.AndroidResourceReference
import org.jetbrains.android.dom.converters.AndroidResourceReferenceBase
import org.jetbrains.android.resourceManagers.LocalResourceManager
import org.jetbrains.android.resourceManagers.ModuleResourceManagers
import org.jetbrains.android.resourceManagers.ResourceManager
import org.jetbrains.android.util.AndroidUtils
import java.util.*

class TextColorContrast : XmlAccessibilityCheck {

    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "Text color contrast",
            description = "",
            link = "https://support.google.com/accessibility/android/answer/7158390"
        )
    }

    override fun availableFor(element: XmlElement): Boolean {
        return element is XmlTag && element.isValid
                && findTextColorAttribute(element) != null
    }

    private fun findTextColorAttribute(tag: XmlTag): XmlAttribute? {
        for (attribute in tag.attributes) {
            if (attribute.name == TEXT_COLOR_ATTR)
                return attribute
        }
        return null
    }

    override fun performCheckOperations(element: XmlElement): List<AccessibilityCheckResult> {
        element as XmlTag

        val textColorAttribute = findTextColorAttribute(element) ?: return emptyList()
        val backgroundColorAttribute = findBackGroundColorAttribute(element) ?: return emptyList()

        val results = mutableListOf<AccessibilityCheckResult>()

        val textColorValue = textColorAttribute.value ?: return emptyList()
        val backgroundColorValue = backgroundColorAttribute.value ?: return emptyList()

        val textColorHex = if (textColorValue == "@color/grey")
            "#999999"
        else
            "#666666"
        val backgroundColorHex = "#EEEEEE"

        val r = ContrastRatioCalculator.getContrastRatio(textColorHex, backgroundColorHex)

        if (r < CONTRAST_BIG_TEXT) {
            results.add(
                AccessibilityCheckResult(
                    type = AccessibilityCheckResultType.ERROR,
                    metadata = null,
                    msg = "Text Color contrast ratio should be at least 3.0:1 for large text. Current: $r:1"
                )
            )
        }

        return results
    }

    // fixme
    private fun getHexColorValueFromResourceUsingReference(tag: XmlTag, colorName: String): String? {
        val androidFacet: AndroidFacet = AndroidFacet.getInstance(tag) ?: return null
        val resourceManager: LocalResourceManager =
            ModuleResourceManagers.getInstance(androidFacet).localResourceManager
        val resourceFiles =
            resourceManager.findResourceFiles(ResourceNamespace.ANDROID, ResourceFolderType.COLOR, null, true, true)
        val resourceFiles2 =
            resourceManager.findResourceFiles(ResourceNamespace.ANDROID, ResourceFolderType.VALUES, null, false, true)

        val allFilesToSearchIn = mutableListOf<PsiFile>()
        allFilesToSearchIn.addAll(resourceFiles)
        allFilesToSearchIn.addAll(resourceFiles2)

        for (file in allFilesToSearchIn) {
            if (file !is XmlFile) {
                continue
            }

            val rootTag = file.rootTag ?: continue
            if (rootTag.name != "resources") {
                continue
            }

            for (childTag in rootTag.subTags) {
                if (childTag.name != "color") {
                    continue
                }

                val nameAttr: XmlAttribute? = childTag.getAttribute("name")
                val valueAttr: String? = childTag.getAttributeValue("name")

                if (nameAttr == null || valueAttr == null) {
                    continue
                }

                val resourceName: String? = nameAttr.value

                if (resourceName == colorName) {
                    return if (valueAttr.startsWith("#")) {
                        // The resource value is already a hex color code.
                        valueAttr
                    } else {
                        // The resource value is a reference to another color resource.
                        null
//                        val resourceManager: ResourceManager? = androidFacet.resourceManager
//                        val referencedColorName: String = resourceValue.substring(1)
//
//                        if (resourceManager != null) {
//                            val referencedColorValue: String? = ResourceHelper.resolveColor(androidFacet, referencedColorName)
//                            return referencedColorValue
//                        }
                    }
                }
            }
        }

        return null
    }

    private fun findBackGroundColorAttribute(tag: XmlTag): XmlAttribute? {
        // fixme: поиск background color у родителей
        for (attribute in tag.attributes) {
            if (attribute.name == BACKGROUND_COLOR_ATTR)
                return attribute
        }
        return null
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }

    companion object {
        /**
         * https://developer.android.com/reference/android/widget/TextView#attr_android:textColor
         */
        private const val TEXT_COLOR_ATTR = "android:textColor"

        private const val BACKGROUND_COLOR_ATTR = "android:background"

        private const val CONTRAST_BIG_TEXT = 3.0
        private const val CONTRAST_SMALL_TEXT = 4.5
        // At least 4.5:1 for small text (below 18 point regular or 14 point bold)
        // At least 3.0:1 for large text (18 point and above regular or 14 point and above bold)

    }
}
