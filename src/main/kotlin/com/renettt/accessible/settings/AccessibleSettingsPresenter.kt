package com.renettt.accessible.settings

import com.intellij.ui.layout.panel
import com.renettt.accessible.BundleProperties
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentListener

class AccessibleSettingsPresenter {

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

    fun createComponent(state: AccessibleState, documentListener: DocumentListener): JComponent {
        defaultTouchTargetSize.apply {
            text = state.minTouchTargetSize.toString() + "dp"
            document.addDocumentListener(documentListener)
        }

        defaultTouchTargetSize.apply {
            text = state.minTouchTargetSizeOverrideForAll.toString()
            document.addDocumentListener(documentListener)
        }

        return panel
    }

    fun getChangedState(state: AccessibleState) {
        state.minTouchTargetSizeOverrideForAll = defaultTouchTargetSizeOverride.text.toIntOrNull()
            ?: state.minTouchTargetSize
    }
}
