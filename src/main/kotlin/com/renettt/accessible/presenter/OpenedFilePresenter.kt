package com.renettt.accessible.presenter

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult
import org.jetbrains.kotlin.psi.KtElement

interface OpenedFilePresenter {

    fun <Element : PsiElement> showMessage(
        element: Element,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>,
        editor: Editor?,
    )

    fun clear()

    fun showMessage(
        lineNumber: Int,
        ktElement: KtElement?,
        checkResultsMap: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>>,
        editor: Editor?,
    )

}
