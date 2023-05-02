package com.renettt.accessible.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AnalyzeAllFilesAction : AnAction() {

    // https://www.youtube.com/watch?v=nJDM9MPV7hc&list=PLJD6kP0BIuwOlvpHQd9YkneVpsG8fMV8W&index=5
    // https://plugins.jetbrains.com/docs/intellij/syntax-highlighter-and-color-settings-page.html#define-a-color-settings-page
    override fun actionPerformed(event: AnActionEvent) {

        NotificationGroupManager.getInstance()
            .getNotificationGroup("AccessibleNotificationGroup")
            .createNotification("Hello from AnalyzeAllFilesAction", NotificationType.INFORMATION)
            .notify(event.project)

//        FileChooser.chooseFile(
//
//        )
//
//        TreeFileChooserFactory.getInstance(event.project)
//            .createFileChooser()

    }

}
