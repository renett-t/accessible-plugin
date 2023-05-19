package com.renettt.accessible.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.renettt.accessible.BundleProperties
import com.renettt.accessible.configure.Configuration
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class AccessibleSettingsManager(
    private val project: Project
) : Configurable, DocumentListener {

    private val state: AccessibleState by lazy {
        AccessibleSettingsService.getInstance(project).state
    }

    private val accessibleSettingsPresenter: AccessibleSettingsPresenter = AccessibleSettingsPresenter()

    private var modified = false

    override fun isModified(): Boolean = modified

    override fun getDisplayName(): String = BundleProperties.message("settings.name")

    override fun apply() {
        accessibleSettingsPresenter.getChangedState(state)

        AccessibleSettingsService.getInstance(project)
            .loadState(state)

        modified = false

        Configuration().settingsChangeEvent(Unit)
    }

    override fun createComponent(): JComponent {
        return accessibleSettingsPresenter.createComponent(state, this)
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
