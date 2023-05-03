package com.renettt.accessible.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service
@State(
    name = "AccessibleConfiguration", storages = [
        Storage(value = "accessibleConfiguration.xml")
    ]
)
class SettingsService : PersistentStateComponent<AccessibleState> {

    private var accessibleState: AccessibleState = AccessibleState()

    override fun getState(): AccessibleState = accessibleState

    override fun loadState(state: AccessibleState) {
        accessibleState = state
    }

    companion object {
        fun getInstance(project: Project): SettingsService =
            project.service()
    }
}
