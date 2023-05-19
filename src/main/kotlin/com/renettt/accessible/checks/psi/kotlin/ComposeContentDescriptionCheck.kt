package com.renettt.accessible.checks.psi.kotlin

import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.ResultMetadata
import com.renettt.accessible.checks.psi.ComposeAccessibilityCheck
import org.jetbrains.kotlin.psi.KtElement
import java.util.*

class ComposeContentDescriptionCheck : ComposeAccessibilityCheck {

    override val metaData: AccessibilityCheckMetaData by lazy {
        AccessibilityCheckMetaData(
            checkId = "ComposeContentDescriptionCheck",
            description = "",
            link = ""
        )
    }

    override fun availableFor(element: KtElement): Boolean {
        // хе-хе
        TODO("Not yet implemented")
    }

    override fun performCheckOperations(element: KtElement): List<AccessibilityCheckResult> {
        TODO("Not yet implemented")
    }

    override fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String {
        TODO("Not yet implemented")
    }
}
