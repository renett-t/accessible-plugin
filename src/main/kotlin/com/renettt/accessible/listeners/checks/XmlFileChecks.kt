package com.renettt.accessible.listeners.checks

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.logging.AccessibleLogger
import com.renettt.accessible.presenter.OpenedFilePresenter

class XmlFileChecks {
    fun performXmlFileCheck(
        file: VirtualFile,
        source: FileEditorManager,
        logger: AccessibleLogger,
        xmlAccessibilityChecksService: XmlAccessibilityChecksService,
        presenter: OpenedFilePresenter?,
        selectedTextEditor: Editor?
    ) {

        // Get the PSI file for the XML file
        val psiFile = PsiManager.getInstance(source.project)
            .findFile(file)

        // Get tags of the PSI file
        val tags = PsiTreeUtil.findChildrenOfType(psiFile, XmlTag::class.java)

        for (tag in tags) {
            val checkRes = xmlAccessibilityChecksService.performChecks(tag)

            logger.log("Performed checks. For: '${tag.name}' in file: '${file.name}'. \n\tChecks: ${checkRes.size}, results: ${checkRes.values.size} $checkRes")
            if (checkRes.isNotEmpty())
                presenter?.showMessage(tag, checkRes, selectedTextEditor)
        }
    }

}
