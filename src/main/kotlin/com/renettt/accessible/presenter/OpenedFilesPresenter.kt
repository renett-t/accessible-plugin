package com.renettt.accessible.presenter

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorGutterAction
import com.intellij.openapi.editor.TextAnnotationGutterProvider
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.JBColor
import icons.Icons
import org.jetbrains.kotlin.idea.core.util.end
import org.jetbrains.kotlin.idea.core.util.start
import java.awt.Color
import java.awt.Cursor
import javax.swing.Icon


class OpenedFilesPresenter(val project: Project) {

    // todo: pass location to show message
    fun showMessage(
        file: VirtualFile,
        tag: XmlTag,
        editor: Editor?,
    ) {
        // find line number where to show gutter and notification
        val lineNumber: Int = findLineNumberForCodeBlock(file, tag)

        val icon: Icon = Icons.defaultIcon
        val tooltipText = "This is a custom message"
        val gutterIconRenderer: GutterIconRenderer = MyGutterIconRenderer(icon, tooltipText, "action1", "action2")

        // Add the gutter icon renderer to the appropriate line of code
        editor?.gutter?.registerTextAnnotation(object : TextAnnotationGutterProvider {
            override fun getLineText(line: Int, editor: Editor?): String? {
                return if (line == lineNumber) {
                    "getLineText"
                } else
                    null
            }

            override fun getToolTip(line: Int, editor: Editor?): String? {
                return if (line == lineNumber) {
                    "getToolTip"
                } else
                    null
            }

            override fun getStyle(line: Int, editor: Editor?): EditorFontType = EditorFontType.CONSOLE_BOLD_ITALIC

            override fun getColor(p0: Int, p1: Editor?): ColorKey = EditorColors.SELECTED_INDENT_GUIDE_COLOR

            override fun getBgColor(p0: Int, p1: Editor?): Color = JBColor.GRAY


            override fun getPopupActions(p0: Int, p1: Editor?): MutableList<AnAction> {
                return mutableListOf(
                    OpenUrlAction().apply {
                        linkDestination = "https://www.youtube.com/watch?v=9uOuocqvTBc"
                    }
                )
            }

            override fun gutterClosed() {
                println(" gutterClosed -> Called when the annotations are removed from the editor gutter.")
            }

        }, object : EditorGutterAction {
            override fun doAction(line: Int) {
                println(" EditorGutterAction.doAction($line)")
            }

            override fun getCursor(line: Int): Cursor? {
                println(" EditorGutterAction.getCursor($line): Cursor")

                return null
            }

        })
    }

    private fun findLineNumberForCodeBlock(file: VirtualFile, tag: XmlTag): Int {
        // Get the document for the file
        val document = FileDocumentManager.getInstance().getDocument(file)
            ?: return -1

        // Get the start offset and line number of the tag
        val textRange = tag.textRange

        val startOffset = tag.textRange.startOffset
        val start = tag.textRange.start
        val endOffset = tag.textRange.endOffset
        val end = tag.textRange.end

        val lineNumber = document.getLineNumber(startOffset)
        val lineCount = document.lineCount
        // Return the line number of the start of the tag
        return lineNumber
    }


    class OpenUrlAction : AnAction(
        "Open Link",
        "Open URL destination in browser",
        Icons.defaultIcon
    ) {

        var linkDestination: String? = null

        override fun actionPerformed(event: AnActionEvent) {
            linkDestination?.apply {
                BrowserUtil.open(this)
            }
        }
    }

    data class MyGutterIconRenderer(
        val icon: Icon,
        val message: String,
        val action1: String,
        val action2: String,
    ) : GutterIconRenderer() {

        init {
            // todo: Listen to tag changes to update the notification message
//                tag.containingFile.viewProvider.document!!.addDocumentListener(object : DocumentAdapter() {
//                    override fun documentChanged(event: DocumentEvent) {
//                        update()
//                    }
//                })
        }

        override fun getClickAction() = null

        override fun isNavigateAction() = false

        override fun getTooltipText() = message

        override fun getIcon(): Icon = icon

    }

}
