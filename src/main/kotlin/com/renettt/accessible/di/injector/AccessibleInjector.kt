package com.renettt.accessible.di.injector

import com.intellij.openapi.project.Project
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksLoader
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.presenter.OpenedFilesPresenter
import com.renettt.accessible.settings.AccessibleState
import com.renettt.accessible.settings.SettingsService

interface AccessibleInjector {
    val xmlAccessibilityChecksLoader: XmlAccessibilityChecksLoader
    val psiXmlAccessibilityChecksService: XmlAccessibilityChecksService
    fun accessibleState(project: Project): AccessibleState
    fun openedFilesPresenter(project: Project): OpenedFilesPresenter
}

class AccessibleInjectorImpl : AccessibleInjector {
    override val xmlAccessibilityChecksLoader: XmlAccessibilityChecksLoader by lazy {
        XmlAccessibilityChecksLoader()
    }

    override val psiXmlAccessibilityChecksService: XmlAccessibilityChecksService by lazy {
        XmlAccessibilityChecksService(xmlAccessibilityChecksLoader).apply {
            init()
        }
    }

    override fun accessibleState(project: Project): AccessibleState {
        return SettingsService.getInstance(project).state
    }

    override fun openedFilesPresenter(project: Project): OpenedFilesPresenter {
        return OpenedFilesPresenter(project)
    }
}
