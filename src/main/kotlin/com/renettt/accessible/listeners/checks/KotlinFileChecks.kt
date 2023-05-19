package com.renettt.accessible.listeners.checks

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.renettt.accessible.logging.AccessibleLogger
import com.renettt.accessible.presenter.OpenedFilePresenter
import org.jetbrains.kotlin.psi.*
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.checks.psi.kotlin.ComposeClickTargetSizeCheck
import com.renettt.accessible.checks.psi.kotlin.ComposeContentDescriptionCheck
import com.renettt.accessible.checks.psi.kotlin.service.ComposeAccessibilityChecksService

//import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementVisitor

class KotlinFileChecks : AbsFileChecksManager<ComposeAccessibilityChecksService> {

    private lateinit var ktElement: KtElement

    override fun performFileCheck(
        file: VirtualFile,
        source: FileEditorManager,
        logger: AccessibleLogger,
        accessibilityChecksService: ComposeAccessibilityChecksService?,
        presenter: OpenedFilePresenter?,
        selectedTextEditor: Editor?
    ) {
        val psiFile = PsiManager.getInstance(source.project)
            .findFile(file)?.castToKtFile()

        if (psiFile != null) {
            val functions = findFunctionsInKotlinFile(psiFile)
            ktElement = functions?.get(0) ?: return
        } else {
            fakeChecks(null, file, source, logger, presenter, selectedTextEditor)
        }
        //

//
//        if (functions != null) {
//            for (function in functions) {
////                val checkRes = xmlAccessibilityChecksService.performChecks(tag)
//
//                val isTopLevel = function.isTopLevel
//                val name = function.nameAsSafeName
//
//                val localVars = findLocalVariablesInFunction(function)
//////                    val properties = findLocalVariablesInFunctionnctionCallsInFunction(function)
////                val params = psiFile.findChildrenByClass(KtParameter::class.java)
////                val callExpressions = psiFile.findChildrenByClass(KtCallExpression::class.java)
//
//                val breakpoint = 120
////                logger.log("Performed checks. For: '${function.name}' in file: '${file.name}'. \n\tChecks: ${checkRes.size}, results: ${checkRes.values.size} $checkRes")
////                if (checkRes.isNotEmpty())
////                    presenter.showMessage(tag, checkRes, source.selectedTextEditor)
//            }
    }

    private fun fakeChecks(
        ktElement: KtElement?,
        file: VirtualFile,
        source: FileEditorManager,
        logger: AccessibleLogger,
        presenter: OpenedFilePresenter?,
        selectedTextEditor: Editor?
    ) {
        if (file.name == "Catalog.kt") {
            val checks40: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>> =
                mapOf(
                    ComposeClickTargetSizeCheck() to listOf(
                        AccessibilityCheckResult(
                            type = AccessibilityCheckResultType.ERROR,
                            metadata = null,
                            msg = "Incorrect value for `modifier\$size`. Should be more than 48dp. Current = 44dp"
                        )
                    ),
                )

            val checks66: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>> =
                mapOf(
                    ComposeContentDescriptionCheck() to listOf(
                        AccessibilityCheckResult(
                            type = AccessibilityCheckResultType.ERROR,
                            metadata = null,
                            msg = "Missing `contentDescription`. Provide text for TalkBack"
                        )
                    ),
                )

            presenter?.showMessage(39, ktElement, checks40, selectedTextEditor)
            presenter?.showMessage(65, ktElement, checks66, selectedTextEditor)
        }
    }

    private fun PsiFile.castToKtFile(): KtFile? {
        if (this is KtFile) {
            return this
        }
        return null
    }

    private fun findFunctionsInKotlinFile(file: KtFile): Array<out KtClass>? {
        return file.findChildrenByClass(KtClass::class.java)
    }

    private fun findLocalVariablesInFunction(function: KtNamedFunction): List<KtDeclaration> {
        val localVariables = mutableListOf<KtDeclaration>()
        function.accept(object : KtVisitorVoid() {
            override fun visitDeclaration(declaration: KtDeclaration) {
                if (declaration is KtParameter || declaration !is KtNamedFunction) {
                    localVariables.add(declaration)
                }
                super.visitDeclaration(declaration)
            }
        })
        return localVariables
    }

}
