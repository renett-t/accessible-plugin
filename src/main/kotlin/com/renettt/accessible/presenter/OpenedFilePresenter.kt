package com.renettt.accessible.presenter

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult

interface OpenedFilePresenter {

    fun <Element : PsiElement> showMessage(
        element: Element,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>,
        editor: Editor?,
    )

//    fun showMessage(
//        tag: XmlTag,
//        checkResultsMap: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>,
//        editor: Editor?,
//    )

}
