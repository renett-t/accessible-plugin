package com.renettt.accessible.presenter

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult

class OpenedFilesPresenter {
    fun showMessage(
        file: VirtualFile,
        tag: XmlTag,
        checkRes: Map<AccessibilityCheck<PsiElement>, List<AccessibilityCheckResult>>
    ) {
        // todo
    }
}
