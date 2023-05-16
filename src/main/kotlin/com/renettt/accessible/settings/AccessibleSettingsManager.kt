package com.renettt.accessible.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import com.renettt.accessible.BundleProperties
import com.renettt.accessible.configure.Configuration
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class AccessibleSettingsManager(
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
        isEditable = true
    }

    private val panel: JPanel = panel {
        row(BundleProperties.message("settings.defaultTouchTargetSize")) {
            defaultTouchTargetSize()
        }
        row(BundleProperties.message("settings.overrideTouchTargetSize")) {
            defaultTouchTargetSizeOverride()
        }

    }

    override fun isModified(): Boolean = modified

    override fun getDisplayName(): String = BundleProperties.message("settings.name")

    override fun apply() {
        state.minTouchTargetSizeOverrideForAll = defaultTouchTargetSizeOverride.text.toIntOrNull()
            ?: state.minTouchTargetSize

        SettingsService.getInstance(project)
            .loadState(state)
        modified = false

        Configuration().settingsChangeEvent(Unit)
    }

    override fun createComponent(): JComponent {
        defaultTouchTargetSize.apply {
            text = state.minTouchTargetSize.toString() + "dp"
            document.addDocumentListener(this@AccessibleSettingsManager)
        }

        defaultTouchTargetSize.apply {
            text = state.minTouchTargetSizeOverrideForAll.toString()
            document.addDocumentListener(this@AccessibleSettingsManager)
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


    interface SettingsChangeEventHandler {
        fun onSettingsChangeUpdate()
    }
}
