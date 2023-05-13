package com.renettt.accessible.configure

import com.intellij.openapi.components.ProjectComponent

class AppComponent: ProjectComponent {
    override fun initComponent() {
        println("Initializing plugin data structures for PROJECT")
    }

    override fun disposeComponent() {
        println("Disposing plugin data structures for PROJECT")
    }

    override fun getComponentName(): String {
        return "myApplicationComponent"
    }
}
