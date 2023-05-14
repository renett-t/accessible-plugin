package com.renettt.accessible.presenter

import com.intellij.codeInsight.hint.HintManager
import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorGutterAction
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.TextAnnotationGutterProvider
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.colors.FontPreferences
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.renettt.accessible.checks.AccessibilityCheck
import com.renettt.accessible.checks.AccessibilityCheckMetaData
import com.renettt.accessible.checks.AccessibilityCheckResult
import icons.Icons
import java.awt.*
import java.awt.Cursor.HAND_CURSOR
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class OpenedFilePresenter(
    private val project: Project,
    private val file: VirtualFile
) : DialogActions {
    private var textAnnotationGutterProviderRegistered = false
    private val messagesToShow = mutableMapOf<Int, List<FileAccessibilityMessage>>()
    private val shownNotifications = mutableSetOf<Int>()
    private val notificationIdsGenerator = NotificationIdsGenerator(1)
    fun showMessage(
        tag: XmlTag,
        checkResultsMap: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>,
        editor: Editor?,
    ) {
        if (checkResultsMap.isEmpty())
            return

        // find line number where to show gutter and notification
//        val lineNumber: Int = findLineNumberForCodeBlock(file, tag)
//
//        val icon: Icon = Icons.defaultIcon
//        val tooltipText = "This is a custom message"
//
//        val gutterIconRenderer: GutterIconRenderer = MyGutterIconRenderer(icon, tooltipText, "action1", "action2")

        if (editor != null) {
            showHintAndGutterIcon(editor, tag, checkResultsMap)
        }
    }

    private fun showHintAndGutterIcon(
        editor: Editor,
        tag: XmlTag,
        checkResultsMap: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>
    ) {
        val document = editor.document
        val tagStartOffset = tag.textOffset
        val lineNumber = document.getLineNumber(tagStartOffset)

        // todo: возвращает неверную дичь
        val editorPosition: Point = getDialogPosition(editor, tag, lineNumber)

        createMessages(tag, checkResultsMap, lineNumber, editorPosition)

        showHints(tag, editorPosition)
        showGutter(editor, checkResultsMap)
    }

    private fun createMessages(
        tag: XmlTag,
        checkResultsMap: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>,
        lineNumber: Int,
        editorPosition: Point
    ) {
        // todo: добавить проверки содержимого сообщений для lineNumber
        //       о чем я: может быть ситуация, когда строки сместились..
        for (entry in checkResultsMap.entries) {
            val (check, results) = entry
            messagesToShow[lineNumber] = createMessagesForCheck(tag, check, results)
        }
    }

    private fun getDialogPosition(editor: Editor, tag: XmlTag, lineNumber: Int): Point {
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


    private fun createMessagesForCheck(
        tag: XmlTag,
        check: AccessibilityCheck<XmlElement>,
        results: List<AccessibilityCheckResult>,
    ): List<FileAccessibilityMessage> {
        val messages = mutableListOf<FileAccessibilityMessage>()
        for (result in results) {
            messages.add(
                FileAccessibilityMessage(
                    forWhom = tag.name,
                    message = result.msg,
                    metadata = check.metaData,
                )
            )
        }
        return messages
    }

    private fun showMessageDialog(
        messages: List<FileAccessibilityMessage>?,
        point: Point,
        desiredSize: Dimension
    ): JDialog? {
        return if (messages.isNullOrEmpty())
            null
        else
            createDialog(messages, point, desiredSize)
    }

    private fun createDialog(messages: List<FileAccessibilityMessage>, point: Point, desiredSize: Dimension): JDialog {
        val actualMessagesToShow = messages.filter { it.lastShownIn !in shownNotifications }

        return AccessibilityMessageDialog(actualMessagesToShow, notificationIdsGenerator.next(), point, desiredSize)
    }

    override fun onDialogShowed(id: Int) {
        shownNotifications.add(id)
    }

    override fun onDialogClosed(id: Int) {
        shownNotifications.remove(id)
    }

    private fun showHints(tag: XmlTag, editorPosition: Point) {
        val flags = HintManager.HIDE_IF_OUT_OF_EDITOR
        val timeOut = 1000
        val onHintHidden = Runnable {
            println("   On Hint Hidden")
        }

//        HintManager.getInstance()
//            .showHint(createJComponent(tag), RelativePoint.fromScreen(editorPosition), flags, timeOut, onHintHidden)
    }

    private fun showGutter(
        editor: Editor,
        checkResultsMap: Map<AccessibilityCheck<XmlElement>, List<AccessibilityCheckResult>>
    ) {
        // Add the gutter icon renderer to the appropriate line of code
//        if (!textAnnotationGutterProviderRegistered) {
        editor.gutter.closeAllAnnotations()
        registerGutterTextAnnotation(editor)
//            textAnnotationGutterProviderRegistered = true
//        }
    }

    private fun registerGutterTextAnnotation(editor: Editor) {
        editor.gutter.registerTextAnnotation(object : TextAnnotationGutterProvider {
            override fun getLineText(line: Int, editor: Editor?): String? {
                return if (line in messagesToShow.keys) {
                    "-->"
                } else
                    null
            }

            override fun getToolTip(line: Int, editor: Editor?): String? {
                return if (line in messagesToShow.keys) {
                    "Here we have some info about accessibility of the element"
                } else
                    null
            }

            override fun getStyle(line: Int, editor: Editor?): EditorFontType = EditorFontType.CONSOLE_BOLD_ITALIC

            override fun getColor(p0: Int, p1: Editor?): ColorKey = EditorColors.SELECTED_INDENT_GUIDE_COLOR

            override fun getBgColor(p0: Int, p1: Editor?): Color = JBColor.GRAY


            override fun getPopupActions(line: Int, p1: Editor?): MutableList<OpenUrlAction> {
                val links = messagesToShow[line]?.map { message ->
                    message.metadata.checkId to message.metadata.link
                }?.map {
                    OpenUrlAction(it.first, it.second)
                }?.toMutableList()

                return links ?: mutableListOf()
            }

            override fun gutterClosed() {
                println(" gutterClosed -> Called when the annotations are removed from the editor gutter.")
            }

        }, object : EditorGutterAction {
            override fun doAction(line: Int) {
                println(" EditorGutterAction.doAction($line)")
            }

            override fun getCursor(line: Int): Cursor? {
                if (line in messagesToShow.keys) {
//                    val popupText = "Your notification message here"
//                    val title = "Notification Title"
//
//                    val factory = JBPopupFactory.getInstance()
//                    val builder = factory.createHtmlTextBalloonBuilder(
//                        popupText, MessageType.WARNING
//                    ) { event ->
//                        val eventType = event?.eventType
//                    }
//
//                    builder.setTitle(title)
//                    builder.setFadeoutTime(5000)
//
//                    val balloon = builder.createBalloon()
//
//                    balloon.show(RelativePoint.fromScreen(editorPosition), Balloon.Position.below)

                    val startOffset = editor.document.getLineStartOffset(line)
                    val relative = editor.offsetToXY(startOffset, false, false)


                    val component: Component = editor.contentComponent
                    val point: Point = relative
                    // я потратила тыщу часов, чтобы понять, что точки относительны компонента и нужно вычислить позицию на родительском экране...
                    SwingUtilities.convertPointToScreen(point, component)
                    point.x = point.x - 100
                    point.y = point.y + 20

                    val desiredSize = Dimension(200, 200)

                    showMessageDialog(messagesToShow[line], point, desiredSize)

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


    data class OpenUrlAction(
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


    // todo:
    private data class FileAccessibilityMessagesBlock(
        val forWhom: String,
        val message: String,
        val metadata: AccessibilityCheckMetaData,
        val editorPosition: Point
    ) {
        // понимаю что жесть какое нарушение - сообщение не должно знать где его отобразили ему откровенно говоря пох-пох,
        // но я не хочу сейчас думать, как сделать лучше
        var lastShownIn: Int = -1
    }

    private data class FileAccessibilityMessage(
        val forWhom: String,
        val message: String,
        val metadata: AccessibilityCheckMetaData,
    ) {
        // понимаю что жесть какое нарушение - сообщение не должно знать где его отобразили ему откровенно говоря пох-пох,
        // но я не хочу сейчас думать, как сделать лучше
        var lastShownIn: Int = -1
    }

    private class NotificationIdsGenerator(private val start: Int) {
        // по-хорошему добавить синхронизаций
        private var counter = start

        fun next(): Int = counter++
    }

    // зафиксировать размер диалога
    // сделать кнопочку закрытия
    private inner class AccessibilityMessageDialog(
        private val messages: List<FileAccessibilityMessage>,
        val id: Int,
        point: Point,
        desiredSize: Dimension
    ) : JDialog() {

        init {
            val panel = JPanel()
            panel.layout =
                GridBagLayout() // http://www.java2s.com/Tutorial/Java/0240__Swing/UsingGridBagConstraints.htm

            var elementsCounter = 0
            for ((index, message) in messages.withIndex()) {
                // Load the image icon from a file
                val icon = Icons.defaultIcon

                // Create a label with some text and an image icon
                val infoLabel = JLabel()
                infoLabel.text = message.message
                infoLabel.icon = icon
//                infoLabel.preferredSize = Dimension(400, infoLabel.preferredSize.height)
                infoLabel.verticalAlignment = SwingConstants.CENTER // align text to the top
                infoLabel.horizontalAlignment = SwingConstants.CENTER // align text to the center

                val gbcForInfoLabel = GridBagConstraints().apply {
                    gridx = 0
                    gridy = elementsCounter++
                    gridwidth = 3
                    anchor = GridBagConstraints.CENTER
                    fill = GridBagConstraints.BOTH
                    ipadx = 10
                    ipady = 10
                }

                val emptyItem = Box.createHorizontalStrut(10)

                val gbcForEmptyItem = GridBagConstraints().apply {
                    gridx = 0
                    gridy = elementsCounter
                    gridwidth = 1
                    weightx = 0.1
                    anchor = GridBagConstraints.BASELINE
                    fill = GridBagConstraints.HORIZONTAL
                }

                val actionLabel = JLabel("<html><a href=\"${message.metadata.link}\">\tFix!</a></html>")
                actionLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                actionLabel.alignmentX = CENTER_ALIGNMENT
                actionLabel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        NotificationGroupManager.getInstance()
                            .getNotificationGroup("AccessibleNotificationGroup")
                            .createNotification("Clicked on 'Fix!' for notification $id", NotificationType.INFORMATION)
                            .notify(project)
                    }
                })

                val gbcForActionLabel = GridBagConstraints().apply {
                    gridx = 1
                    gridy = elementsCounter
                    gridwidth = 1
                    weightx = 0.45
                    anchor = GridBagConstraints.LINE_START
                    fill = GridBagConstraints.HORIZONTAL
                    ipadx = 20
                }


                val linkLabel = JLabel("<html><a href=\"${message.metadata.link}\">Specs\t</a></html>")
                linkLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                linkLabel.alignmentX = CENTER_ALIGNMENT
                linkLabel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            Desktop.getDesktop().browse(java.net.URI.create(message.metadata.link))
                        }
                    }
                })

                val gbcForLinkLabel = GridBagConstraints().apply {
                    gridx = 2
                    gridy = elementsCounter++
                    gridwidth = 1
                    weightx = 0.45
                    anchor = GridBagConstraints.LINE_START
                    fill = GridBagConstraints.HORIZONTAL
                }

                val emptyItemBottom = Box.createVerticalStrut(10)

                val gbcForEmptyItemBottom = GridBagConstraints().apply {
                    gridx = 0
                    gridy = elementsCounter++
                    gridwidth = 1
                    weightx = 1.0
                    fill = GridBagConstraints.HORIZONTAL
                }

                panel.add(infoLabel, gbcForInfoLabel)

                panel.add(emptyItem, gbcForEmptyItem)
                panel.add(actionLabel, gbcForActionLabel)
                panel.add(linkLabel, gbcForLinkLabel)

                panel.add(emptyItemBottom, gbcForEmptyItemBottom)

                message.lastShownIn = id
            }

            this.add(panel, BorderLayout.CENTER)

            // Remove the close button from the title bar
            this.isUndecorated = true
            this.location = point
            this.pack()

            // Create a timer to close the frame after 3 seconds
            val delay = 3000 // milliseconds

            val taskPerformer = ActionListener {
                this.dispose()
                this@OpenedFilePresenter.onDialogClosed(id)
            }

            val timer = Timer(delay, taskPerformer)
            timer.start()

            // Add a mouse listener to reset the timer when the user moves the mouse pointer over the frame
            this.addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    timer.stop()
                    timer.start()
                }
            })

//            this.size = desiredSize
            this.isVisible = true
            this@OpenedFilePresenter.onDialogShowed(id)
        }

    }

}
