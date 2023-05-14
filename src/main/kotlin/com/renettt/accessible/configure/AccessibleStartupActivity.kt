package com.renettt.accessible.configure

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class AccessibleStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        println("Initializing plugin data structures for Project. StartupActivity ><")
        Configuration().loadProject(project)
        Configuration().setReady(true)
    }
}
