package com.renettt.accessible.di.injector

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksLoader
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.logging.AccessibleLogger
import com.renettt.accessible.logging.impl.AccessibleLoggerImpl
import com.renettt.accessible.notifications.AccessibleNotificationManager
import com.renettt.accessible.notifications.impl.AccessibleNotificationManagerImpl
import com.renettt.accessible.presenter.impl.OpenedFilePresenterImpl
import com.renettt.accessible.settings.AccessibleState
import com.renettt.accessible.settings.SettingsService

interface AccessibleInjector {

    val ready: Boolean
    val project: Project
    fun loadProject(project: Project)
    fun setReady(ready: Boolean)

    val xmlAccessibilityChecksLoader: XmlAccessibilityChecksLoader

    val psiXmlAccessibilityChecksService: XmlAccessibilityChecksService

    val notificationManager: AccessibleNotificationManager

    val logger: AccessibleLogger

    fun accessibleState(project: Project): AccessibleState
    fun openedFilesPresenter(project: Project, file: VirtualFile): OpenedFilePresenterImpl
}

class AccessibleInjectorImpl : AccessibleInjector {
    private lateinit var _project: Project
    override var ready: Boolean = false
        private set
    override val project: Project
        get() = _project

    override fun loadProject(project: Project) {
        this._project = project
    }

    override fun setReady(ready: Boolean) {
        this.ready = ready
    }

    override val xmlAccessibilityChecksLoader: XmlAccessibilityChecksLoader by lazy {
        XmlAccessibilityChecksLoader()
    }

    override val psiXmlAccessibilityChecksService: XmlAccessibilityChecksService by lazy {
        XmlAccessibilityChecksService(xmlAccessibilityChecksLoader).apply {
            init()
        }
    }

    override val notificationManager: AccessibleNotificationManager by lazy {
        AccessibleNotificationManagerImpl()
    }

    override val logger: AccessibleLogger by lazy {
        AccessibleLoggerImpl()
    }

    override fun accessibleState(project: Project): AccessibleState {
        return SettingsService.getInstance(project).state
    }

    override fun openedFilesPresenter(project: Project, file: VirtualFile): OpenedFilePresenterImpl {
        return OpenedFilePresenterImpl(project, file, notificationManager)
    }
}
