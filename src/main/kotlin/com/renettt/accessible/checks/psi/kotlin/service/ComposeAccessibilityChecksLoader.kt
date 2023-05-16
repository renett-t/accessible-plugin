package com.renettt.accessible.checks.psi.kotlin.service

import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityChecksLoader
import com.renettt.accessible.checks.psi.kotlin.ComposeClickTargetSizeCheck
import com.renettt.accessible.checks.psi.kotlin.ComposeContentDescriptionCheck
import org.jetbrains.kotlin.psi.KtElement

class ComposeAccessibilityChecksLoader<El> : AccessibilityChecksLoader<El> where El : KtElement, El : PsiElement {
    override fun load(): List<AccessibilityCheck<El>> {
        return listOf()
    }
}
