package com.renettt.accessible.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.layout.panel
import com.renettt.accessible.BundleProperties
import java.awt.Color
import java.awt.Container
import java.net.URL
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class AccessibleSettings(
    private val project: Project
) : Configurable, DocumentListener {

    private val state: AccessibleState by lazy {
        SettingsService.getInstance(project).state
    }

    private var modified = false

    private val defaultTouchTargetSize: JTextField = JTextField().apply {
        isEditable = false
    }
    private val defaultTouchTargetSizeOverride: JTextField = JTextField().apply {
        isEditable = false
    }

//    private val addButton: JButton = JButton().apply {
//        icon = ImageIcon(javaClass.classLoader.getResource("icons/add.png"))
//    }

    private val panel: JPanel = panel {
        row(BundleProperties.message("settings.defaultTouchTargetSize")) {
            defaultTouchTargetSize()
        }
        row {
//            addButton()
        }

    }

    override fun isModified(): Boolean = modified

    override fun getDisplayName(): String = BundleProperties.message("settings.name")

    override fun apply() {
//        state.apiKey = String(apiKeyField.password)
//        state.token = String(tokenField.password)
//        state.fromListId = fromListIdField.text
//        state.toListId = toListIdField.text

        SettingsService.getInstance(project)
            .loadState(state)
        modified = false
    }

    override fun createComponent(): JComponent {
        defaultTouchTargetSize.apply {
            text = state.minTouchTargetSize
            document.addDocumentListener(this@AccessibleSettings)
        }

        return panel
    }

    override fun changedUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun insertUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun removeUpdate(e: DocumentEvent?) {
        modified = true
    }
}
