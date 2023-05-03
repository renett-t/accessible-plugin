package com.renettt.accessible.di.injector

import com.intellij.openapi.project.Project
import com.renettt.accessible.checks.PsiAccessibilityChecksService
import com.renettt.accessible.presenter.OpenedFilesPresenter
import com.renettt.accessible.settings.AccessibleState
import com.renettt.accessible.settings.SettingsService

interface AccessibleInjector {
    val psiXmlAccessibilityChecksService: PsiAccessibilityChecksService
    fun accessibleState(project: Project): AccessibleState
    fun openedFilesPresenter(project: Project): OpenedFilesPresenter
}

class AccessibleInjectorImpl : AccessibleInjector {

    override val psiXmlAccessibilityChecksService: PsiAccessibilityChecksService by lazy {
        PsiAccessibilityChecksService().apply {
            initialize()
        }
    }

    override fun accessibleState(project: Project): AccessibleState {
        return SettingsService.getInstance(project).state
    }

    override fun openedFilesPresenter(project: Project): OpenedFilesPresenter {
        return OpenedFilesPresenter(project)
    }
}
