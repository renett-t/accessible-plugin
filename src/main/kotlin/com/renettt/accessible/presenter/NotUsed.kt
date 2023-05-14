package com.renettt.accessible.presenter

import com.intellij.openapi.editor.markup.GutterIconRenderer
import javax.swing.Icon

/**
 * Not used but interesting
 */
data class MyGutterIconRenderer(
    val gutterIcon: Icon,
    val message: String,
    val action1: String,
    val action2: String,
) : GutterIconRenderer() {

    init {
        // Listen to tag changes to update the notification message
//                tag.containingFile.viewProvider.document!!.addDocumentListener(object : DocumentAdapter() {
//                    override fun documentChanged(event: DocumentEvent) {
//                        update()
//                    }
//                })
    }

    override fun getClickAction() = null

    override fun isNavigateAction() = false

    override fun getTooltipText() = message

    override fun getIcon(): Icon = gutterIcon

}
