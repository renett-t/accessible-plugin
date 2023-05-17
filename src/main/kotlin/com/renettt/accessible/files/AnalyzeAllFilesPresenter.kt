package com.renettt.accessible.files

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ui.JBUI
import icons.Icons
import java.awt.*
import java.awt.Component.CENTER_ALIGNMENT
import java.awt.Component.LEFT_ALIGNMENT
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


interface AnalyzeAllFilesPresenter {
    fun showResults(checkResults: List<PsiElementChecksPerFile>, project: Project)
}

class AnalyzeAllFilesPresenterImpl : AnalyzeAllFilesPresenter {

    override fun showResults(checkResults: List<PsiElementChecksPerFile>, project: Project) {
        // Create and set up the window.
        val frame = JFrame("Accessible plugin: Results")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        // Set up main panel to hold all elements
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)

        for (check in checkResults) {
            when (check) {
                is ComposeElementChecksPerFile ->
                    addComponentsToPane(mainPanel, check, { file ->
                        openVirtualFileInEditor(project, file)
                    }) { link ->
                        openLinkInBrowser(project, link)
                    }

                is XmlElementChecksPerFile ->
                    addComponentsToPaneXml(mainPanel, check, { file ->
                        openVirtualFileInEditor(project, file)
                    }) { link ->
                        openLinkInBrowser(project, link)
                    }
            }
        }

        // Create a JScrollPane and set the panel as its viewport view
        val scrollPane = JScrollPane(mainPanel)

        // Set the preferred size of the scroll pane
        scrollPane.preferredSize = Dimension(900, 1200)

        // Set the scroll bar policies (optional)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED

        frame.contentPane.add(scrollPane)

        // Display the window.
        frame.pack()
        frame.isVisible = true
    }

    private fun addComponentsToPaneXml(
        pane: Container,
        checksPerFile: XmlElementChecksPerFile,
        openFileAction: (file: VirtualFile) -> Unit,
        openBrowserAction: (link: String) -> Unit
    ) {
        val panel = JPanel().apply {
            layout = GridBagLayout()
        }
        val icon = Icons.defaultIcon

        val constraints = GridBagConstraints()

        // Create a label with some text and an image icon - FILE TITLE
        val fileTitleLabel =
            JLabel("<html><a href=\"${checksPerFile.file.path}\">${checksPerFile.file.name}\t</a></html>")
        fileTitleLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        fileTitleLabel.alignmentX = CENTER_ALIGNMENT
        fileTitleLabel.icon = icon
        fileTitleLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(mouseEvent: MouseEvent) {
                openFileAction(checksPerFile.file)
            }
        })

        // GridBagConstraints.HORIZONTAL -> natural height, maximum width
        constraints.apply {
            fill = GridBagConstraints.HORIZONTAL
            gridy = 0
            gridx = 0
            insets = JBUI.insets(10)
        }

        panel.add(fileTitleLabel, constraints)


        val contentMessagesPanel = JPanel()
        contentMessagesPanel.layout = GridBagLayout()

        for ((index, elementCheck) in checksPerFile.list.withIndex()) {
            val count = elementCheck.checks.entries.flatMap { entry ->
                entry.value
            }.size

            // тайтл элемента
            val elementTitleLabel = JLabel(getDisplayedNameForXmlTag(elementCheck.forWhom))

            val gbcForElementTitleLabel = GridBagConstraints().apply {
                gridx = 0
                gridy = index
                weightx = 0.2
                gridheight = count
                anchor = GridBagConstraints.NORTH
                fill = GridBagConstraints.HORIZONTAL
            }

            contentMessagesPanel.add(elementTitleLabel, gbcForElementTitleLabel)

            // сообщения для элемента
            var counterForElement = 0
            for (entry in elementCheck.checks.entries) {
                for (check in entry.value) {
                    val messageLabel = JTextArea()
                    messageLabel.text = check.msg
                    messageLabel.alignmentX = LEFT_ALIGNMENT

                    val gbcForMessageLabel = GridBagConstraints().apply {
                        gridx = 1
                        gridy = index + counterForElement
                        weightx = 0.6
                        gridheight = 1
                        insets = JBUI.insets(5)
                        fill = GridBagConstraints.HORIZONTAL
                    }

                    contentMessagesPanel.add(messageLabel, gbcForMessageLabel)

                    // ссылка на спеку
                    val messageLinkLabel = JLabel("Specs")
                    val gbcForMessageLinkLabel = GridBagConstraints().apply {
                        gridx = 2
                        gridy = index + counterForElement++
                        weightx = 0.2
                        gridheight = 1
                        anchor = GridBagConstraints.NORTH
                        insets = JBUI.insets(5)
                        fill = GridBagConstraints.HORIZONTAL
                    }
                    messageLinkLabel.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(mouseEvent: MouseEvent) {
                            openBrowserAction(entry.key.metaData.link)
                        }
                    })

                    contentMessagesPanel.add(messageLinkLabel, gbcForMessageLinkLabel)
                }
            }

        }

        panel.add(contentMessagesPanel, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        val emptyItemBottom = Box.createVerticalStrut(10)
        val gbcForEmptyItemBottom = GridBagConstraints().apply {
            gridx = 0
            gridy = 2
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        }
        panel.add(emptyItemBottom, gbcForEmptyItemBottom)

        pane.add(panel)
    }

    private fun getDisplayedNameForXmlTag(forWhom: XmlElement): String {
        if (forWhom is XmlTag && forWhom.isValid) {
            for (attr in forWhom.attributes) {
                if (attr.name == "android:id")
                    return attr.value ?: "unknown"
            }
        } else {
            return forWhom.text.substring(0, 20)
        }

        return "unknown"
    }

    // allFilesPanel.compose.name=Compose
    // allFilesPanel.xml.name=Xml
    // allFilesPanel.file.title=File:
    // allFilesPanel.file.forWhom=Elements:
    // allFilesPanel.file.checkResults=Results:

    private fun addComponentsToPane(
        pane: Container,
        checksPerFile: ComposeElementChecksPerFile,
        openFileAction: (file: VirtualFile) -> Unit,
        openBrowserAction: (link: String) -> Unit
    ) {
        val panel = JPanel().apply {
            layout = GridBagLayout()
        }
        val icon = Icons.defaultIcon

        val constraints = GridBagConstraints()

        // Create a label with some text and an image icon - FILE TITLE
        val fileTitleLabel =
            JLabel("<html><a href=\"${checksPerFile.file.path}\">${checksPerFile.file.name}\t</a></html>")
        fileTitleLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        fileTitleLabel.alignmentX = CENTER_ALIGNMENT
        fileTitleLabel.icon = icon
        fileTitleLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(mouseEvent: MouseEvent) {
                openFileAction(checksPerFile.file)
            }
        })

        // GridBagConstraints.HORIZONTAL -> natural height, maximum width
        constraints.apply {
            fill = GridBagConstraints.HORIZONTAL
            gridy = 0
            gridx = 0
            insets = JBUI.insets(10)
        }

        panel.add(fileTitleLabel, constraints)


        val contentMessagesPanel = JPanel()
        contentMessagesPanel.layout = GridBagLayout()

        for ((index, elementCheck) in checksPerFile.list.withIndex()) {
            val count = elementCheck.checks.entries.flatMap { entry ->
                entry.value
            }.size

            // тайтл элемента
            val elementTitleLabel = JLabel(forWhomFromCompose(elementCheck.forWhom))

            val gbcForElementTitleLabel = GridBagConstraints().apply {
                gridx = 0
                gridy = index
                weightx = 0.2
                gridheight = count
                anchor = GridBagConstraints.NORTH
                fill = GridBagConstraints.HORIZONTAL
            }

            contentMessagesPanel.add(elementTitleLabel, gbcForElementTitleLabel)

            // сообщения для элемента
            var counterForElement = 0
            for (entry in elementCheck.checks.entries) {
                for (check in entry.value) {
                    val messageLabel = JTextArea()
                    messageLabel.text = check.msg
                    messageLabel.alignmentX = LEFT_ALIGNMENT

                    val gbcForMessageLabel = GridBagConstraints().apply {
                        gridx = 1
                        gridy = index + counterForElement
                        weightx = 0.6
                        gridheight = 1
                        fill = GridBagConstraints.HORIZONTAL
                        insets = JBUI.insets(5)
                    }

                    contentMessagesPanel.add(messageLabel, gbcForMessageLabel)

                    // ссылка на спеку
                    val messageLinkLabel = JLabel("Specs")
                    val gbcForMessageLinkLabel = GridBagConstraints().apply {
                        gridx = 2
                        gridy = index + counterForElement++
                        weightx = 0.2
                        gridheight = 1
                        anchor = GridBagConstraints.NORTH
                        fill = GridBagConstraints.HORIZONTAL
                        insets = JBUI.insets(5)
                    }
                    messageLinkLabel.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(mouseEvent: MouseEvent) {
                            openBrowserAction(entry.key.metaData.link)
                        }
                    })

                    contentMessagesPanel.add(messageLinkLabel, gbcForMessageLinkLabel)

                }
            }

        }

        panel.add(contentMessagesPanel, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })

        val emptyItemBottom = Box.createVerticalStrut(10)
        val gbcForEmptyItemBottom = GridBagConstraints().apply {
            gridx = 0
            gridy = 2
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        }
        panel.add(emptyItemBottom, gbcForEmptyItemBottom)

        pane.add(panel)
    }

    private fun forWhomFromCompose(forWhom: Int): String {
        return when (forWhom) {
            39 -> "Button (39)"
            65 -> "Icon (63)"
            else -> "unknown"
        }
    }

    private fun openVirtualFileInEditor(project: Project, virtualFile: VirtualFile) {
        FileEditorManager
            .getInstance(project)
            .openFile(virtualFile, true)
    }

    private fun openLinkInBrowser(project: Project, link: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(java.net.URI.create(link))
        }
    }

}
