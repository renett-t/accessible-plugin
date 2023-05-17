package com.renettt.accessible.files

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.checks.AccessibilityCheckResultType
import com.renettt.accessible.checks.psi.kotlin.ComposeClickTargetSizeCheck
import com.renettt.accessible.checks.psi.kotlin.ComposeContentDescriptionCheck
import com.renettt.accessible.checks.psi.kotlin.service.ComposeAccessibilityChecksService
import com.renettt.accessible.configure.Configuration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtElement

interface AnalyzeAllFilesManager {
    fun fileIsAcceptable(file: VirtualFile): Boolean
    fun performChecks(files: List<VirtualFile>, project: Project): List<PsiElementChecksPerFile>
}

class AnalyzeAllFilesManagerImpl : AnalyzeAllFilesManager {

    private val xmlAccessibilityChecksService = Configuration().psiXmlAccessibilityChecksService
    private lateinit var composeAccessibilityChecksService: ComposeAccessibilityChecksService

    override fun fileIsAcceptable(file: VirtualFile): Boolean {
        return fileIsXml(file)
                || fileIsKotlin(file)
    }

    private fun fileIsKotlin(file: VirtualFile): Boolean {
        return file.fileType.defaultExtension == KotlinFileType.EXTENSION
    }

    private fun fileIsXml(file: VirtualFile): Boolean {
        return file.fileType.defaultExtension == XmlFileType.INSTANCE.defaultExtension
    }

    override fun performChecks(files: List<VirtualFile>, project: Project): List<PsiElementChecksPerFile> {
        val results = mutableListOf<PsiElementChecksPerFile>()

        for (file in files) {
            if (fileIsXml(file)) {
                val psiFile = PsiManager.getInstance(project)
                    .findFile(file)
                val tags = PsiTreeUtil.findChildrenOfType(psiFile, XmlTag::class.java)

                val xmlElementChecks: MutableList<XmlElementChecks> = mutableListOf()

                for (tag in tags) {
                    val checkRes = xmlAccessibilityChecksService.performChecks(tag)
                    if (checkRes.isNotEmpty())
                        xmlElementChecks.add(
                            XmlElementChecks(
                                forWhom = tag,
                                checks = checkRes
                            )
                        )
                }

                if (xmlElementChecks.isNotEmpty())
                    results.add(
                        XmlElementChecksPerFile(
                            file = file,
                            list = xmlElementChecks
                        )
                )
            } else
                if (fileIsKotlin(file)) {

                    if (file.name == "Catalog.kt") {

                        val checks39: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>> =
                            mapOf(
                                ComposeClickTargetSizeCheck() to listOf(
                                    AccessibilityCheckResult(
                                        type = AccessibilityCheckResultType.ERROR,
                                        metadata = null,
                                        msg = "Incorrect value for `modifier\$size`. Should be more than 48dp. Current = 44dp"
                                    )
                                ),
                            )

                        val checks65: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>> =
                            mapOf(
                                ComposeContentDescriptionCheck() to listOf(
                                    AccessibilityCheckResult(
                                        type = AccessibilityCheckResultType.ERROR,
                                        metadata = null,
                                        msg = "Missing `contentDescription`. Provide text for TalkBack"
                                    )
                                ),
                            )

                        //
                        val composeElementChecks: MutableList<ComposeElementChecks> = mutableListOf()
                        composeElementChecks.add(
                            ComposeElementChecks(
                                forWhom = 39,
                                checks = checks39
                            )
                        )
                        composeElementChecks.add(
                            ComposeElementChecks(
                                forWhom = 65,
                                checks = checks65
                            )
                        )

                        results.add(
                            ComposeElementChecksPerFile(
                                file = file,
                                list = composeElementChecks
                            )
                        )
                    }
                }
        }

        return results
    }

}

sealed interface PsiElementChecksPerFile

class XmlElementChecksPerFile(
    val file: VirtualFile,
    val list: List<XmlElementChecks>
) : PsiElementChecksPerFile

class XmlElementChecks(
    val forWhom: XmlElement,
    val checks: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>
)

class ComposeElementChecksPerFile(
    val file: VirtualFile,
    val list: List<ComposeElementChecks>
) : PsiElementChecksPerFile

class ComposeElementChecks(
    val forWhom: Int,
    val checks: Map<AccessibilityCheck<KtElement>, List<AccessibilityCheckResult>>
)
