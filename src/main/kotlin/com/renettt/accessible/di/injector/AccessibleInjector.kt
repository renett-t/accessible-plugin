package com.renettt.accessible.di.injector

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksLoader
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.files.*
import com.renettt.accessible.listeners.checks.KotlinFileChecks
import com.renettt.accessible.listeners.checks.XmlFileChecks
import com.renettt.accessible.listeners.file.OpenedFileListenerRegistry
import com.renettt.accessible.logging.AccessibleLogger
import com.renettt.accessible.logging.impl.AccessibleLoggerImpl
import com.renettt.accessible.notifications.AccessibleNotificationManager
import com.renettt.accessible.notifications.impl.AccessibleNotificationManagerImpl
import com.renettt.accessible.presenter.impl.OpenedFilePresenterImpl
import com.renettt.accessible.settings.AccessibleSettingsManager
import com.renettt.accessible.settings.AccessibleState
import com.renettt.accessible.settings.AccessibleSettingsService
import com.renettt.accessible.utils.event.ObservableEvent

interface AccessibleInjector {

    val ready: Boolean
    val project: Project
    fun loadProject(project: Project)
    fun setReady(ready: Boolean)

    val xmlFileChecks: XmlFileChecks
    val kotlinFileChecks: KotlinFileChecks

    val xmlAccessibilityChecksLoader: XmlAccessibilityChecksLoader

    val psiXmlAccessibilityChecksService: XmlAccessibilityChecksService

    val notificationManager: AccessibleNotificationManager

    val logger: AccessibleLogger

    fun accessibleState(project: Project): AccessibleState

    val settingsChangeEvent: ObservableEvent<AccessibleSettingsManager.SettingsChangeEventHandler, AccessibleInjector, Unit>

    val openedFileListenerRegistry: OpenedFileListenerRegistry

    fun openedFilesPresenter(project: Project, file: VirtualFile): OpenedFilePresenterImpl

    // analyze all files
    val filesManager: FilesManager

    val analyzeAllFilesManager: AnalyzeAllFilesManager

    val analyzeAllFilesPresenter: AnalyzeAllFilesPresenter
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

    override val xmlFileChecks: XmlFileChecks by lazy {
        XmlFileChecks()
    }
    override val kotlinFileChecks: KotlinFileChecks by lazy {
        KotlinFileChecks()
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
        return AccessibleSettingsService.getInstance(project).state
    }

    override val settingsChangeEvent: ObservableEvent<AccessibleSettingsManager.SettingsChangeEventHandler, AccessibleInjector, Unit> by lazy {
        object :
            ObservableEvent<AccessibleSettingsManager.SettingsChangeEventHandler, AccessibleInjector, Unit>(this) {
            override fun notifyHandler(
                handler: AccessibleSettingsManager.SettingsChangeEventHandler,
                sender: AccessibleInjector,
                args: Unit
            ) {
                handler.onSettingsChangeUpdate()
            }
        }
    }
    override val openedFileListenerRegistry: OpenedFileListenerRegistry by lazy {
        OpenedFileListenerRegistry()
    }

    override fun openedFilesPresenter(project: Project, file: VirtualFile): OpenedFilePresenterImpl {
        return OpenedFilePresenterImpl(project, file, notificationManager)
    }

    override val filesManager: FilesManager by lazy {
        FilesManagerImpl()
    }

    override val analyzeAllFilesManager: AnalyzeAllFilesManager by lazy {
        AnalyzeAllFilesManagerImpl()
    }

    override val analyzeAllFilesPresenter: AnalyzeAllFilesPresenter by lazy {
        AnalyzeAllFilesPresenterImpl()
    }
}
