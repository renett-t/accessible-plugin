package com.renettt.accessible.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.renettt.accessible.BundleProperties
import com.renettt.accessible.configure.Configuration
import com.renettt.accessible.listeners.file.OpenXmlFileListener
import com.renettt.accessible.di.DI


@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    private val configuration = Configuration()

    init {
        thisLogger().info(BundleProperties.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")

        configuration.configure()
        initServices(project)
    }

    private fun initServices(project: Project) {
//        FileEditorManager.getInstance(project)
//            .addFileEditorManagerListener(MyFileEditorManagerListener())
        val listener = OpenXmlFileListener(
            project = project,
            accessibilityChecksService = DI(),
            presenter = DI()
        )

        val messageBus = project.messageBus
        messageBus.connect()
            .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)


    }

    fun getRandomNumber() = (1..100).random()
}
