package com.renettt.accessible.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.renettt.accessible.configure.Configuration

class AnalyzeAllFilesAction : AnAction() {

    private val analyzeAllFilesManager = Configuration().analyzeAllFilesManager
    private val analyzeAllFilesPresenter = Configuration().analyzeAllFilesPresenter
    private val filesManager = Configuration().filesManager
    private val notificationManager = Configuration().notificationManager

    // https://www.youtube.com/watch?v=nJDM9MPV7hc&list=PLJD6kP0BIuwOlvpHQd9YkneVpsG8fMV8W&index=5
    // https://plugins.jetbrains.com/docs/intellij/syntax-highlighter-and-color-settings-page.html#define-a-color-settings-page
    override fun actionPerformed(event: AnActionEvent) {

        val project = event.project ?: return
        val projectPath = project.basePath ?: return

        val filesToAnalyze = filesManager.findAllChildren(
            path = projectPath,
            criteria = {
                analyzeAllFilesManager.fileIsAcceptable(it)
            }
        )

        val checkResults = analyzeAllFilesManager.performChecks(filesToAnalyze, project)

        val breakPoint1 = 932

        analyzeAllFilesPresenter.showResults(checkResults, project)
    }

}
