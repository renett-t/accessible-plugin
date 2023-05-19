package com.renettt.accessible.files

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager

interface FilesManager {
    fun findAllChildren(path: String, criteria: (VirtualFile) -> Boolean): List<VirtualFile>
}

class FilesManagerImpl : FilesManager {
    override fun findAllChildren(path: String, criteria: (VirtualFile) -> Boolean): List<VirtualFile> {
//        val projectFile = FilenameIndex.getVirtualFilesByName(projectPath, GlobalSearchScope.projectScope(project))
        val filesToAnalyze = mutableListOf<VirtualFile>()

        // Get the VirtualFileManager instance
        val virtualFileManager = VirtualFileManager.getInstance()

        // Find the VirtualFile representing the project directory
        val projectDirectory = virtualFileManager.findFileByUrl("file://$path")

        // Get all children VirtualFiles
        val children = projectDirectory?.children

        // Visit all children recursively
        projectDirectory?.let { directory ->
            visitVirtualFilesRecursively(directory) { file ->
                // Do something with each visited file -- add to list
                if (criteria(file))
                    filesToAnalyze.add(file)
            }
        }

        return filesToAnalyze
    }

    private fun visitVirtualFilesRecursively(file: VirtualFile, visitor: (VirtualFile) -> Unit) {
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
