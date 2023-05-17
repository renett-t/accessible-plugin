package com.renettt.accessible.listeners.file

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.renettt.accessible.checks.psi.kotlin.service.ComposeAccessibilityChecksService
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.configure.Configuration
import com.renettt.accessible.listeners.checks.KotlinFileChecks
import com.renettt.accessible.listeners.checks.XmlFileChecks
import com.renettt.accessible.presenter.OpenedFilePresenter
import com.renettt.accessible.settings.AccessibleSettingsManager
import org.jetbrains.kotlin.idea.KotlinFileType


class OpenedFileListener(
    private val project: Project,
) : FileEditorManagerListener, AccessibleSettingsManager.SettingsChangeEventHandler {

    private val visibleFiles: MutableSet<VirtualFile> = mutableSetOf()
    init {
        Configuration().settingsChangeEvent += this
    }
    private val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)

    private val xmlAccessibilityChecksService: XmlAccessibilityChecksService =
        Configuration().psiXmlAccessibilityChecksService
    private lateinit var composeAccessibilityChecksService: ComposeAccessibilityChecksService

    private val xmlFileChecks = Configuration().xmlFileChecks
    private val kotlinFileChecks = Configuration().kotlinFileChecks

    private val openedFileListenerRegistry = OpenedFileListenerRegistry()

    private val notificationManager = Configuration().notificationManager

    private val logger = Configuration().logger


    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        logger.log("File opened: $file")
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        notificationManager.showNotification(
            project,
            "Accessible Info",
            "Closed file: ${file.name}",
            NotificationType.INFORMATION
        )

        openedFileListenerRegistry.unregister(file)
        openedFileListenerRegistry[file]?.documentListener?.let {
            source.selectedTextEditor?.document?.removeDocumentListener(it)
        }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val newFile = event.newFile
        val oldFile = event.oldFile

        visibleFiles.add(newFile)
        visibleFiles.remove(oldFile)

        logger.log("selectionChanged: new=$newFile, old=$oldFile")

        performChecks(newFile, event.manager)
    }

    private fun performChecks(file: VirtualFile, fileEditorManager: FileEditorManager) {
        if (!Configuration().ready)
            return

        // Check if the file is an XML file
        if (file.fileType === XmlFileType.INSTANCE) {
            onXmlFileOpened(fileEditorManager, file)
        } else if (file.fileType == JavaFileType.INSTANCE) {
            notificationManager.showNotification(
                project,
                "Accessible Info",
                "Opened java file",
                NotificationType.INFORMATION
            )


        } else if (file.fileType.defaultExtension == KotlinFileType.EXTENSION) {
            onKotlinFileOpened(fileEditorManager, file)
        } else {
            val unknownFileType = file.fileType
            val unknownFileTypeN = file.fileType.javaClass.canonicalName
        }
    }

    private fun onKotlinFileOpened(source: FileEditorManager, file: VirtualFile) {
        notificationManager.showNotification(
            project,
            "Accessible Info",
            "Opened kotlin file",
            NotificationType.INFORMATION
        )

        registerPresenterAndListenersForFile(file, source, openedFileListenerRegistry)
        openedFileListenerRegistry[file]?.presenter?.clear()
        kotlinFileChecks.performFileCheck(file, source, logger, null, openedFileListenerRegistry[file]?.presenter, source.selectedTextEditor)
    }

    private fun onXmlFileOpened(source: FileEditorManager, file: VirtualFile) {
        notificationManager.showNotification(
            project,
            "Accessible Info",
            "Opened xml file",
            NotificationType.INFORMATION
        )

        registerPresenterAndListenersForFile(file, source, openedFileListenerRegistry)
        openedFileListenerRegistry[file]?.presenter?.clear()
        xmlFileChecks.performFileCheck(file, source, logger, xmlAccessibilityChecksService, openedFileListenerRegistry[file]?.presenter, source.selectedTextEditor)
    }

    private fun registerPresenterAndListenersForFile(
        file: VirtualFile,
        source: FileEditorManager,
        openedFileListenerRegistry: OpenedFileListenerRegistry
    ) {
        val docChangeListener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                // fixme: забаговано. Нужно добавить структуру которая будет отменять предыдущий чек
                // если пришел запрос на новый а предыдущий ещё выполняется
                performChecks(file, source)
            }

            override fun bulkUpdateFinished(document: Document) {

            }
        }

        openedFileListenerRegistry.register(
            file,
            OpenedFileListenerRegistry.Managers(
                presenter = Configuration().openedFilesPresenter(project, file),
                documentListener = docChangeListener
            )
        )

        source.selectedTextEditor?.document?.addDocumentListener(docChangeListener)
    }


    private class OpenedFileListenerRegistry {
        private val registry = hashMapOf<String, Managers>()

        fun register(file: VirtualFile, managers: Managers) {
            registry[getFileKey(file)] = managers
        }

        fun unregister(file: VirtualFile) {
            registry.remove(getFileKey(file))
        }

        operator fun get(file: VirtualFile): Managers? {
            return registry[getFileKey(file)]
        }

        private fun getFileKey(file: VirtualFile): String {
            return file.path
        }

        data class Managers(
            val presenter: OpenedFilePresenter,
            val documentListener: DocumentListener,
        )
    }

    override fun onSettingsChangeUpdate() {
        for (file in visibleFiles) {
            performChecks(file, fileEditorManager)
        }
    }

}
