package com.renettt.accessible.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AnalyzeCurrentFileAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        NotificationGroupManager.getInstance()
            .getNotificationGroup("AccessibleNotificationGroup")
            .createNotification("Hello from AnalyzeCurrentFileAction", NotificationType.INFORMATION)
            .notify(event.project)
    }

}
