package com.renettt.accessible.presenter

import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import java.awt.Point


/**
 * СД для хранения блока сообщений, предназначенных для отображения в конкретной position
 */
internal data class FileAccessibilityMessagesBlock(
    val messages: List<FileAccessibilityMessage>,
    val position: Point
) {
    // понимаю что жесть какое нарушение - сообщение не должно знать где его отобразили ему откровенно говоря пох-пох,
    // но я не хочу сейчас думать, как сделать лучше
    var lastShownIn: Int = -1
}

internal data class FileAccessibilityMessage(
    val forWhom: PsiElement,
    val message: String,
    val metadata: AccessibilityCheckMetaData,
)
