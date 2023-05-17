package com.renettt.accessible.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.renettt.accessible.configure.Configuration

class AnalyzeAllFilesAction : AnAction() {

    private val analyzeAllFilesManager = Configuration().analyzeAllFilesManager
    private val analyzeAllFilesPresenter = Configuration().analyzeAllFilesPresenter
    private val notificationManager = Configuration().notificationManager

    // https://www.youtube.com/watch?v=nJDM9MPV7hc&list=PLJD6kP0BIuwOlvpHQd9YkneVpsG8fMV8W&index=5
    // https://plugins.jetbrains.com/docs/intellij/syntax-highlighter-and-color-settings-page.html#define-a-color-settings-page
    override fun actionPerformed(event: AnActionEvent) {

        val project = event.project ?: return
        val projectPath = project.basePath ?: return

        val projectFile = FilenameIndex.getVirtualFilesByName(projectPath, GlobalSearchScope.projectScope(project))

        val filesToAnalyze = mutableListOf<VirtualFile>()


// Get the VirtualFileManager instance
        val virtualFileManager = VirtualFileManager.getInstance()

// Find the VirtualFile representing the project directory
        val projectDirectory = virtualFileManager.findFileByUrl("file://$projectPath")

// Get all children VirtualFiles
        val children = projectDirectory?.children

// Visit all children recursively
        projectDirectory?.let { directory ->
            visitVirtualFilesRecursively(directory) { file ->
                // Do something with each visited file
                if (analyzeAllFilesManager.fileIsAcceptable(file))
                    filesToAnalyze.add(file)
            }
        }

//        for (sourceSet in projectFile) {
//            VfsUtilCore.iterateChildrenRecursively(sourceSet, object : VirtualFileFilter {
//                override fun accept(vF: VirtualFile): Boolean {
//                    return analyzeAllFilesManager.fileIsAcceptable(vF)
//                }
//            }, object : ContentIterator {
//                override fun processFile(vF: VirtualFile): Boolean {
//                    filesToAnalyze.add(vF)
//                    return true
//                }
//
//            })
//        }

        val checkResults = analyzeAllFilesManager.performChecks(filesToAnalyze, project)

        val breakPoint1 = 932

        analyzeAllFilesPresenter.showResults(checkResults, project)
    }


    fun visitVirtualFilesRecursively(file: VirtualFile, visitor: (VirtualFile) -> Unit) {
        if (!file.isDirectory) {
            // Base case: If it's not a directory, visit the file
            visitor(file)
        } else {
            // Recursive case: If it's a directory, visit all children recursively
            for (child in file.children) {
                visitVirtualFilesRecursively(child, visitor)
            }
        }
    }



}
