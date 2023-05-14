package com.renettt.accessible.notifications

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

interface AccessibleNotificationManager {

    fun showNotification(
        context: Project,
        title: String,
        content: String,
        notificationType: NotificationType,
    )

}
