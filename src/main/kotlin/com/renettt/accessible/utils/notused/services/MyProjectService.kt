package com.renettt.accessible.utils.notused.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.renettt.accessible.BundleProperties
import com.renettt.accessible.configure.Configuration


@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    private val configuration = Configuration()

    init {
        thisLogger().info(BundleProperties.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")

//        configuration.configure()
//        initServices(project)
        println("Configuration done.")
    }

    private fun initServices(project: Project) {
//        FileEditorManager.getInstance(project)
//            .addFileEditorManagerListener(MyFileEditorManagerListener())
//        val listener = OpenXmlFileListener(
//            project = project,
//        )
//
//        val messageBus = project.messageBus
//        messageBus.connect()
//            .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)
//

    }

    fun getRandomNumber() = (1..100).random()
}
