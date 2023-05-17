package com.renettt.accessible.listeners.checks

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.renettt.accessible.logging.AccessibleLogger
import com.renettt.accessible.presenter.OpenedFilePresenter

interface AbsFileChecksManager<AccessibilityChecksService> {

    fun performFileCheck(
        file: VirtualFile,
        source: FileEditorManager,
        logger: AccessibleLogger,
        accessibilityChecksService: AccessibilityChecksService?,
        presenter: OpenedFilePresenter?,
        selectedTextEditor: Editor?
    )

}
