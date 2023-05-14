package com.renettt.accessible.notifications.impl

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.renettt.accessible.notifications.AccessibleNotificationManager


class AccessibleNotificationManagerImpl : AccessibleNotificationManager {

    override fun showNotification(
        context: Project,
        title: String,
        content: String,
        notificationType: NotificationType,
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("AccessibleNotificationGroup")
            .createNotification(
                title, content, notificationType
            )
            .notify(context)
    }

    // //                    val popupText = "Your notification message here"
    ////                    val title = "Notification Title"
    ////
    ////                    val factory = JBPopupFactory.getInstance()
    ////                    val builder = factory.createHtmlTextBalloonBuilder(
    ////                        popupText, MessageType.WARNING
    ////                    ) { event ->
    ////                        val eventType = event?.eventType
    ////                    }
    ////
    ////                    builder.setTitle(title)
    ////                    builder.setFadeoutTime(5000)
    ////
    ////                    val balloon = builder.createBalloon()
    ////
    ////                    balloon.show(RelativePoint.fromScreen(editorPosition), Balloon.Position.below)
}

