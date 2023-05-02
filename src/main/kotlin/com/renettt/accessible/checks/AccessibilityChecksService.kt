package com.renettt.accessible.checks

import com.intellij.psi.PsiElement

class PsiAccessibilityChecksService {

    private lateinit var checks: List<AccessibilityCheck<PsiElement>>
    fun initialize(): List<AccessibilityCheck<PsiElement>> {
        checks = emptyList()
        return checks
    }

    fun performChecks(
//        checks: List<AccessibilityCheck<PsiElement>>,
        element: PsiElement
    ): Map<AccessibilityCheck<PsiElement>, List<AccessibilityCheckResult>> {
        val results = mutableMapOf<AccessibilityCheck<PsiElement>, List<AccessibilityCheckResult>>()
        for (check in checks) {
            val checkResult = check.runCheck(element)
            results[check] = checkResult
        }
        return results
    }

}
