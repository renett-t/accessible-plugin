package com.renettt.accessible.presenter

import com.intellij.codeInsight.hint.HintManager
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorGutterAction
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.TextAnnotationGutterProvider
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import icons.Icons
import org.jetbrains.kotlin.idea.core.util.end
import org.jetbrains.kotlin.idea.core.util.start
import java.awt.Color
import java.awt.Cursor
import java.awt.Cursor.HAND_CURSOR
import java.awt.Font
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener


class OpenedFilesPresenter(val project: Project) {

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

        if (editor != null) {
            showHintAndGutterIcon(editor, tag)
        }
    }

    private fun showHintAndGutterIcon(editor: Editor, tag: XmlTag) {
        val document = editor.document
        val tagStartOffset = tag.textOffset
        val lineNumber = document.getLineNumber(tagStartOffset)
        val editorPosition: Point = editor.visualPositionToXY(editor.offsetToVisualPosition(tagStartOffset))

        showHint(tag, editorPosition)
        showGutterIcon(editor, editorPosition, lineNumber)
    }

    private fun showHint(tag: XmlTag, editorPosition: Point) {

        val flags = HintManager.HIDE_IF_OUT_OF_EDITOR
        val timeOut = 1000
        val onHintHidden = Runnable {
            println("   On Hint Hidden")
        }

        HintManager.getInstance()
            .showHint(createJComponent(tag), RelativePoint.fromScreen(editorPosition), flags, timeOut, onHintHidden)
    }

    private fun showGutterIcon(editor: Editor, editorPosition: Point, lineNumber: Int) {
        // Add the gutter icon renderer to the appropriate line of code
        editor.gutter.registerTextAnnotation(object : TextAnnotationGutterProvider {
            override fun getLineText(line: Int, editor: Editor?): String? {
                return if (line == lineNumber) {
                    "->"
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

                if (line == lineNumber) {
                    val popupText = "Your notification message here"
                    val title = "Notification Title"

                    val factory = JBPopupFactory.getInstance()
                    val builder = factory.createHtmlTextBalloonBuilder(
                        popupText, MessageType.WARNING
                    ) { event ->
                        val eventType = event?.eventType
                    }

                    builder.setTitle(title)
                    builder.setFadeoutTime(5000)

                    val balloon = builder.createBalloon()

                    balloon.show(RelativePoint.fromScreen(editorPosition), Balloon.Position.below)


                    return Cursor.getPredefinedCursor(HAND_CURSOR)
                }
                return null
            }

        })
    }

    private fun createJComponent(tag: XmlTag): JComponent {
        val font = Font("Arial", Font.BOLD, 12)
        val textColor = JBColor(Color.BLACK, JBColor.RED.darker())

        val panel = JPanel()
        val textAttributes = TextAttributes(textColor, null, null, EffectType.WAVE_UNDERSCORE, 0)
        panel.foreground = JBColor.BLACK
        panel.background = JBColor.WHITE
        panel.font = font
        panel.add(
            JLabel("Notification message for tag '${tag.name}'", JLabel.CENTER).apply {
                foreground = textColor
                setFont(font)
            })

        panel.addMouseListener(object : MouseAdapter() {

            override fun mouseClicked(e: MouseEvent) {
                // Navigate to the tag in the editor when clicked
                val file = tag.containingFile
                val virtualFile = file.virtualFile
                if (virtualFile != null) {
                    val editor = FileEditorManager.getInstance(tag.project).selectedTextEditor
                    if (editor != null && virtualFile == PsiUtilCore.getVirtualFile(file)) {
                        val element = tag.navigationElement
                        val offset = element.textOffset
                        val length = element.textLength
                        editor.caretModel.moveToOffset(offset)
                        editor.selectionModel.setSelection(offset, offset + length)
                        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
                    }
                }
            }

            override fun mouseEntered(e: MouseEvent) {
                panel.foreground = textColor
            }

            override fun mouseExited(e: MouseEvent) {
                panel.foreground = JBColor.BLACK
            }
        })

        return panel
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

    fun ready() {
        println("Presenter: READY")
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
        val gutterIcon: Icon,
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

        override fun getIcon(): Icon = gutterIcon

    }

}
