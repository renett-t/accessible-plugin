package com.renettt.accessible.listeners.file

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.configure.Configuration
import com.renettt.accessible.presenter.impl.OpenedFilePresenterImpl
import org.jetbrains.kotlin.idea.KotlinFileType


class OpenedFileListener(
    private val project: Project,
) : FileEditorManagerListener {

    private val xmlAccessibilityChecksService: XmlAccessibilityChecksService =
        Configuration().psiXmlAccessibilityChecksService

    private val openedFileListenerRegistry = OpenedFileListenerRegistry()

    private val notificationManager = Configuration().notificationManager


    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (!Configuration().ready)
            return

        // Check if the file is an XML file
        if (file.fileType === XmlFileType.INSTANCE) {
            registerPresenterForFile(file, openedFileListenerRegistry)

            notificationManager.showNotification(project, "Accessible Info", "Opened xml file", NotificationType.INFORMATION)

            // Get the PSI file for the XML file
            val psiFile = PsiManager.getInstance(source.project)
                .findFile(file)

            // Get tags of the PSI file
            val tags = PsiTreeUtil.findChildrenOfType(psiFile, XmlTag::class.java)

            for (tag in tags) {
                val checkRes = xmlAccessibilityChecksService.performChecks(tag)

                // debugging fields
//                val locationData = tag.metaData
//
//                val editors = source.getEditors(file)
//                val selectedEditor = source.getSelectedEditor(file)
//                val allEditors = source.getAllEditors(file)
//                val ed = source.selectedTextEditor

                source.selectedTextEditor?.document?.addDocumentListener(object : DocumentListener {

                    override fun documentChanged(event: DocumentEvent) {

                    }

                    override fun bulkUpdateFinished(document: Document) {
                        notificationManager.showNotification(project, "Accessible Info", "bulkUpdateFinished event, newFragment $document", NotificationType.INFORMATION)
                    }
                })

                // todo: добавить логи
                if (checkRes.isNotEmpty())
                    openedFileListenerRegistry[file]
                        ?.showMessage(tag, checkRes, source.selectedTextEditor)
            }
        } else if (file.fileType == JavaFileType.INSTANCE) {
            notificationManager.showNotification(project, "Accessible Info", "Opened java file", NotificationType.INFORMATION)



        } else if (file.fileType == KotlinFileType.INSTANCE) {
            notificationManager.showNotification(project, "Accessible Info", "Opened kotlin file", NotificationType.INFORMATION)

        } else {
            val unknownFileType = file.fileType
            val unknownFileTypeN = file.fileType.javaClass.canonicalName
        }
    }

    private fun registerPresenterForFile(file: VirtualFile, openedFileListenerRegistry: OpenedFileListenerRegistry) {
        openedFileListenerRegistry.register(
            file,
            Configuration().openedFilesPresenter(project, file)
        )
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        notificationManager.showNotification(project, "Accessible Info", "Closed file: ${file.name}", NotificationType.INFORMATION)

        openedFileListenerRegistry.unregister(file)
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        // todo: тут нужно убирать слушателя изменений последнего открытого файла
        super.selectionChanged(event)
    }

    private class OpenedFileListenerRegistry {
        private val registry = hashMapOf<String, OpenedFilePresenterImpl>()

        fun register(file: VirtualFile, openedFilePresenter: OpenedFilePresenterImpl) {
            registry[getFileKey(file)] = openedFilePresenter
        }

        fun unregister(file: VirtualFile) {
            registry.remove(getFileKey(file))
        }

        operator fun get(file: VirtualFile): OpenedFilePresenterImpl? {
            return registry[getFileKey(file)]
        }

        private fun getFileKey(file: VirtualFile): String {
            return file.path
        }
    }

}
