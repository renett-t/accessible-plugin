package com.renettt.accessible.presenter.impl

import com.intellij.codeInsight.hint.HintManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorGutterAction
import com.intellij.openapi.editor.TextAnnotationGutterProvider
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckResult
import com.renettt.accessible.notifications.AccessibleNotificationManager
import com.renettt.accessible.presenter.*
import com.renettt.accessible.presenter.AccessibilityMessageAnnotationDialog
import com.renettt.accessible.presenter.FileAccessibilityMessage
import com.renettt.accessible.presenter.FileAccessibilityMessagesBlock
import com.renettt.accessible.presenter.OpenUrlAction
import java.awt.*
import java.awt.Cursor.HAND_CURSOR
import javax.swing.*

/**
 * MessageAnnotationDialog - содержит информацию о проверках Accessibility, скрытых за Gutter's annotationa
 */
class OpenedFilePresenterImpl(
    private val project: Project,
    private val file: VirtualFile,
    private val notificationManager: AccessibleNotificationManager
) : OpenedFilePresenter, DialogActions {

    private val messagesToShow = mutableMapOf<Int, FileAccessibilityMessagesBlock>()

    private val shownMessageAnnotationDialogs = mutableSetOf<Int>()
    private val messageAnnotationDialogIdsGenerator = MessageAnnotationDialogIdsGenerator(1)

    companion object {
        private const val NOTIFICATION_TITLE = "Accessible Checks"
    }

    override fun <Element : PsiElement> showMessage(
        element: Element,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>,
        editor: Editor?
    ) {
        if (checkResultsMapForElement.isEmpty())
            return

        if (editor != null) {
            showHintsAndGutterIcons(editor, element, checkResultsMapForElement)
        }
    }

    private fun <Element : PsiElement> showHintsAndGutterIcons(
        editor: Editor,
        element: Element,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>
    ) {
        val document = editor.document
        val elementStartOffset = element.textOffset
        val lineNumber = document.getLineNumber(elementStartOffset)

        val editorPosition: Point = getDialogPositionInEditor(editor, element, lineNumber)

        createMessages(element, checkResultsMapForElement, lineNumber, editorPosition)

        prepareHints(element, editorPosition)
        prepareGutter(editor, checkResultsMapForElement)
    }

    private fun <Element : PsiElement> createMessages(
        element: Element,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>,
        lineNumber: Int,
        editorPosition: Point
    ) {
        // fixme: Возможно нужно предусмотреть проверки, ибо при редактировании файла lineNumber мог сместиться.
        for (entry in checkResultsMapForElement.entries) {
            val (check, results) = entry
            messagesToShow[lineNumber] = createAccessibilityMessagesBlock(element, check, results, editorPosition)
        }
    }

    /**
     * Возвращает точку в Editor для lineNumber.
     * Относительная для компонента Editor, НЕ абсолютная для экрана
     */
    private fun getDialogPositionInEditor(editor: Editor, element: PsiElement, lineNumber: Int): Point {
        // Get the start and end offsets for the given line number
        val lineStartOffset = editor.document.getLineStartOffset(lineNumber)
        val lineEndOffset = editor.document.getLineEndOffset(lineNumber)

        // Get the bounds of the text range for the given line
        val rectangle = editor.visualPositionToXY(editor.offsetToVisualPosition(lineStartOffset))
        val rectangleEnd = editor.visualPositionToXY(editor.offsetToVisualPosition(lineEndOffset))

        // Calculate the x and y coordinates for the message
        val x = rectangleEnd.x
        val y = rectangleEnd.y

        println("Coordinates: rectangle $rectangle")
        println("Coordinates: rectangleEnd $rectangleEnd")
        println("Coordinates: $x, $y")

        return Point(x, y)
    }


    private fun <Element : PsiElement> createAccessibilityMessagesBlock(
        element: Element,
        check: AccessibilityCheck<Element>,
        results: List<AccessibilityCheckResult>,
        editorPosition: Point,
    ): FileAccessibilityMessagesBlock {
        val messages = mutableListOf<FileAccessibilityMessage>()
        for (result in results) {
            messages.add(
                FileAccessibilityMessage(
                    forWhom = element,
                    message = result.msg,
                    metadata = check.metaData,
                )
            )
        }
        return FileAccessibilityMessagesBlock(
            messages = messages,
            position = editorPosition
        )
    }

    private fun showMessageDialog(
        messagesBlock: FileAccessibilityMessagesBlock?,
        point: Point,
        desiredSize: Dimension
    ): JDialog? {
        return if (messagesBlock == null || messagesBlock.messages.isEmpty())
            null
        else
            createMessageAnnotationDialog(messagesBlock, point, desiredSize)
    }

    private fun createMessageAnnotationDialog(
        messagesBlock: FileAccessibilityMessagesBlock,
        point: Point,
        desiredSize: Dimension
    ): JDialog? {
        if (messagesBlock.lastShownIn in shownMessageAnnotationDialogs)
            return null

        val messageAnnotationDialogId = messageAnnotationDialogIdsGenerator.next()
        return AccessibilityMessageAnnotationDialog(
            id = messageAnnotationDialogId,
            messagesBlock = messagesBlock,
            point = point,
            desiredSize = desiredSize,
            onShowedListener = {
                onDialogShowed(messageAnnotationDialogId)
            },
            onActionClickedListener = {
                notificationManager.showNotification(
                    project,
                    NOTIFICATION_TITLE,
                    "Clicked on action for notification $messagesBlock",
                    NotificationType.INFORMATION
                )
            },
            onOpenLinkClickedListener = { _, url ->
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(java.net.URI.create(url))
                }
            },
            onClosedListener = {
                onDialogClosed(messageAnnotationDialogId)
            }
        )
    }

    override fun onDialogShowed(id: Int) {
        shownMessageAnnotationDialogs.add(id)
    }

    override fun onDialogClosed(id: Int) {
        shownMessageAnnotationDialogs.remove(id)
    }

    private fun prepareHints(element: PsiElement, editorPosition: Point) {
        val flags = HintManager.HIDE_IF_OUT_OF_EDITOR
        val timeOut = 1000
        val onHintHidden = Runnable {
            println("   On Hint Hidden")
        }

//        HintManager.getInstance()
//            .showHint(createJComponent(tag), RelativePoint.fromScreen(editorPosition), flags, timeOut, onHintHidden)
    }

    private fun <Element : PsiElement> prepareGutter(
        editor: Editor,
        checkResultsMapForElement: Map<AccessibilityCheck<Element>, List<AccessibilityCheckResult>>
    ) {
        editor.gutter.closeAllAnnotations()
        registerGutterTextAnnotation(editor)
    }

    private fun registerGutterTextAnnotation(editor: Editor) {
        editor.gutter.registerTextAnnotation(object : TextAnnotationGutterProvider {
            override fun getLineText(line: Int, editor: Editor?): String? {
                return if (line in messagesToShow.keys) {
                    "\uD83D\uDC8C"
                } else
                    null
            }

            override fun getToolTip(line: Int, editor: Editor?): String? {
                return if (line in messagesToShow.keys) {
                    val forWhom: Set<String>? = messagesToShow[line]?.messages?.map {
                        it.forWhom
                    }?.map {
                        it.toString()
                    }?.toSet()

                    "Here we have some info about accessibility of element: $forWhom"
                } else
                    null
            }

            override fun getStyle(line: Int, editor: Editor?): EditorFontType = EditorFontType.CONSOLE_BOLD_ITALIC

            override fun getColor(p0: Int, p1: Editor?): ColorKey = EditorColors.SELECTED_INDENT_GUIDE_COLOR

            override fun getBgColor(p0: Int, p1: Editor?): Color = EditorColors.GUTTER_BACKGROUND.defaultColor


            override fun getPopupActions(line: Int, p1: Editor?): MutableList<OpenUrlAction> {
                val links = messagesToShow[line]?.messages?.map { message ->
                    message.metadata.checkId to message.metadata.link
                }?.map {
                    OpenUrlAction(it.first, it.second)
                }?.toMutableList()

                return links ?: mutableListOf()
            }

            override fun gutterClosed() {
                notificationManager.showNotification(
                    project,
                    NOTIFICATION_TITLE,
                    "Closed Annotations. To see all check results again, click action on toolbar",
                    NotificationType.INFORMATION
                )
            }

        }, object : EditorGutterAction {
            override fun doAction(line: Int) {
                println(" EditorGutterAction.doAction($line). Пока так и не поняла зачем и для чего")
            }

            override fun getCursor(line: Int): Cursor? {
                if (line in messagesToShow.keys) {

                    val messageDialogPoint = calculateNonRelativePoint(editor, line)
                    // по приколу передается, сейчас диалогу пох-пох на desiredSize
                    val desiredSize = Dimension(200, 200)

                    showMessageDialog(messagesToShow[line], messageDialogPoint, desiredSize)

                    return Cursor.getPredefinedCursor(HAND_CURSOR)
                }

                return null
            }

        })
    }

    private fun calculateNonRelativePoint(editor: Editor, line: Int): Point {
        val startOffset = editor.document.getLineStartOffset(line)
        val relative = editor.offsetToXY(startOffset, false, false)

        val component: Component = editor.contentComponent
        val point: Point = relative
        // fun fact: я потратила тыщу часов, чтобы понять,
        // что точки относительны компонента и нужно вычислить позицию на родительском экране...
        SwingUtilities.convertPointToScreen(point, component)

        // Эти манипуляции для того, чтобы отображать диалог чуть ниже/выше иконки и наведенного курсора
        point.x = point.x - 100
        point.y = point.y + 20

        return point
    }

}
