package com.renettt.accessible.checks.psi.kotlin.service

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityChecksLoader
import org.jetbrains.kotlin.psi.KtElement

class ComposeAccessibilityChecksLoader : AccessibilityChecksLoader<PsiMethod> {
    override fun load(): List<AccessibilityCheck<PsiMethod>> {
        return listOf()
    }
}
