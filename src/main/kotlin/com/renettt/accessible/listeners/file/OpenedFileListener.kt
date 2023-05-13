package com.renettt.accessible.listeners.file

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.renettt.accessible.checks.psi.xml.service.XmlAccessibilityChecksService
import com.renettt.accessible.configure.Configuration
import com.renettt.accessible.presenter.OpenedFilesPresenter
import org.jetbrains.kotlin.idea.KotlinFileType


class OpenedFileListener(
    private val project: Project,
) : FileEditorManagerListener {

    private val xmlAccessibilityChecksService: XmlAccessibilityChecksService =
        Configuration().psiXmlAccessibilityChecksService

    val listener = object : VirtualFileListener {

        override fun contentsChanged(event: VirtualFileEvent) {
            super.contentsChanged(event)
        }

    }

    private val filePresenter: OpenedFilesPresenter = Configuration().openedFilesPresenter(project)

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (!Configuration().ready)
            return

        // Check if the file is an XML file
        if (file.fileType === XmlFileType.INSTANCE) {

            NotificationGroupManager.getInstance()
                .getNotificationGroup("AccessibleNotificationGroup")
                .createNotification("Opened xml file", NotificationType.INFORMATION)
                .notify(project)

            // Get the PSI file for the XML file
            val psiFile = PsiManager.getInstance(source.project)
                .findFile(file)

            // Get tags of the PSI file
            val tags = PsiTreeUtil.findChildrenOfType(psiFile, XmlTag::class.java)

            for (tag in tags) {
                val checkRes = xmlAccessibilityChecksService.performChecks(tag)
                val locationData = tag.metaData

                val editors = source.getEditors(file)
                val selectedEditor = source.getSelectedEditor(file)
                val allEditors = source.getAllEditors(file)
                val ed = source.selectedTextEditor

                ed?.document?.addDocumentListener(object : DocumentListener {

                    override fun documentChanged(event: DocumentEvent) {
                        NotificationGroupManager.getInstance()
                            .getNotificationGroup("AccessibleNotificationGroup")
                            .createNotification("documentChanged event, newFragment ${event.newFragment}", NotificationType.INFORMATION)
                            .notify(project)
                    }
                })

                filePresenter.showMessage(file, tag, source.selectedTextEditor)
            }
        } else if (file.fileType == JavaFileType.INSTANCE) {

            NotificationGroupManager.getInstance()
                .getNotificationGroup("AccessibleNotificationGroup")
                .createNotification("Opened java file", NotificationType.INFORMATION)
                .notify(project)

        } else if (file.fileType == KotlinFileType.INSTANCE) {

            NotificationGroupManager.getInstance()
                .getNotificationGroup("AccessibleNotificationGroup")
                .createNotification("Opened Kotlin file", NotificationType.INFORMATION)
                .notify(project)
        } else {
            val unknownFileType = file.fileType
            val unknownFileTypeN = file.fileType.javaClass.canonicalName
        }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        super.fileClosed(source, file)
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {

        super.selectionChanged(event)
    }
}
