package com.renettt.accessible.checks.psi

import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityChecksLoader

abstract class PsiAccessibilityChecksService<PsiCheckElement : PsiElement>(
    private val checksLoader: AccessibilityChecksLoader<PsiCheckElement>
) {

    private lateinit var checks: List<AccessibilityCheck<PsiCheckElement>>

    fun init() {
        this.checks = loadChecks()
    }

    private fun loadChecks(): List<AccessibilityCheck<PsiCheckElement>> = checksLoader.load()

    fun performChecks(element: PsiCheckElement): Map<AccessibilityCheck<PsiCheckElement>, List<AccessibilityCheckResult>> {
        val results = mutableMapOf<AccessibilityCheck<PsiCheckElement>, List<AccessibilityCheckResult>>()
        for (check in checks) {
            val checkResult = check.runCheck(element)
            results[check] = checkResult
        }

        return results
    }

}
