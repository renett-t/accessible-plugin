package com.renettt.accessible.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.roots.*
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.*

fun getSourceRoots(project: Project): List<VirtualFile> {
    val projectRootManager = ProjectRootManager.getInstance(project)
    val sourceRoots = mutableListOf<VirtualFile>()
    for (sourceRoot in projectRootManager.contentSourceRoots) {
        sourceRoots.add(sourceRoot)
    }
    return sourceRoots
}
//
//fun getAllFilesFromSourceRoots(project: Project): List<VirtualFile> {
//    val sourceRoots = getSourceRoots(project)
//    val allFiles = mutableListOf<VirtualFile>()
//
//    for (sourceRoot in sourceRoots) {
//        VfsUtilCore.visitChildrenRecursively(sourceRoot, object : VirtualFileVisitor() {
//            override fun visitFile(file: VirtualFile): Boolean {
//                allFiles.add(file)
//                return super.visitFile(file)
//            }
//        })
//    }
//
//    return allFiles
//}
//
//fun getResourceFiles(project: Project): List<VirtualFile> {
//    val projectBasePath = project.basePath ?: return emptyList()
//    val resDir = VfsUtilCore.findRelativeFile(projectBasePath, "res") ?: return emptyList()
//    val resourceFiles = mutableListOf<VirtualFile>()
//    VfsUtilCore.visitChildrenRecursively(resDir, object : VirtualFileVisitor() {
//        override fun visitFile(file: VirtualFile): Boolean {
//            resourceFiles.add(file)
//            return super.visitFile(file)
//        }
//    })
//    return resourceFiles
//}
//
