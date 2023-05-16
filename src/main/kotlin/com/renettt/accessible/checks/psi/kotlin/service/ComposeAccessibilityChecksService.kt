package com.renettt.accessible.checks.psi.kotlin.service

import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.psi.PsiAccessibilityChecksService
import org.jetbrains.kotlin.psi.KtElement

class ComposeAccessibilityChecksService<El>(
    checksLoader: ComposeAccessibilityChecksLoader<El>
) : PsiAccessibilityChecksService<El>(checksLoader) where El : KtElement, El : PsiElement
