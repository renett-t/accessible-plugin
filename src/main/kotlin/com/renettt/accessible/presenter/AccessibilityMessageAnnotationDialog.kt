package com.renettt.accessible.presenter

import icons.Icons
import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


/**
 * Диалог для отображения результатов проверок
 */
// зафиксировать размер диалога
// сделать кнопочку закрытия
internal class AccessibilityMessageAnnotationDialog(
    val id: Int,
    messagesBlock: FileAccessibilityMessagesBlock,
    point: Point,
    desiredSize: Dimension,
    delayMs: Long = 3000,
    onShowedListener: () -> Unit,
    onActionClickedListener: (mouseEvent: MouseEvent) -> Unit,
    onOpenLinkClickedListener: (mouseEvent: MouseEvent, url: String) -> Unit,
    onClosedListener: () -> Unit,
) : JDialog() {

    init {
        val panel = JPanel()
        panel.layout =
            GridBagLayout() // http://www.java2s.com/Tutorial/Java/0240__Swing/UsingGridBagConstraints.htm

        var elementsCounter = 0
        for ((index, message) in messagesBlock.messages.withIndex()) {
            // Load the image icon from a file
            val icon = Icons.defaultIcon

            // Create a label with some text and an image icon
            val infoLabel = JLabel()
            infoLabel.text = message.message
            infoLabel.icon = icon
//                infoLabel.preferredSize = Dimension(400, infoLabel.preferredSize.height)
            // осуждаю что нет очевидного способа ограничить ширину вьюхи
            infoLabel.verticalAlignment = SwingConstants.CENTER // align text to the top
            infoLabel.horizontalAlignment = SwingConstants.LEFT // align text to the center

            val gbcForInfoLabel = GridBagConstraints().apply {
                gridx = 0
                gridy = elementsCounter++
                gridwidth = 3
                anchor = GridBagConstraints.LINE_START
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
                override fun mouseClicked(mouseEvent: MouseEvent) {
                    onActionClickedListener(mouseEvent)
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
                override fun mouseClicked(mouseEvent: MouseEvent) {
                    onOpenLinkClickedListener(mouseEvent, message.metadata.link)
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

            // Установить, в какой нотификации блок был показан в последний раз.
            // Позволяет избежать отображения лишних нотификаций.
            messagesBlock.lastShownIn = id
        }

        this.add(panel, BorderLayout.CENTER)

        // Remove the close button from the title bar
        this.isUndecorated = true
        this.location = point
        this.pack()

        // Create a timer to close the frame after 3 seconds
        val taskPerformer = ActionListener {
            this.dispose()
            onClosedListener()
        }

        val timer = Timer(delayMs.toInt(), taskPerformer)
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
        onShowedListener()
    }
}
