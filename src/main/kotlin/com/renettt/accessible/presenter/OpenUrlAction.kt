package com.renettt.accessible.presenter

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import icons.Icons

internal data class OpenUrlAction(
    val text: String,
    val linkDestination: String,
) : AnAction(
    text,
    "Open URL destination in browser",
    Icons.defaultIcon
) {
    // чет не понял зачем нужен no args constructor
    constructor() : this("", "")

    override fun actionPerformed(event: AnActionEvent) {
        linkDestination.apply {
            BrowserUtil.open(this)
        }
    }
}
