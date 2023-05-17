package com.renettt.accessible.listeners.file

import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.vfs.VirtualFile
import com.renettt.accessible.presenter.OpenedFilePresenter

class OpenedFileListenerRegistry {
    private val registry = hashMapOf<String, Managers>()

    fun register(file: VirtualFile, managers: Managers) {
        registry[getFileKey(file)] = managers
    }

    fun unregister(file: VirtualFile) {
        registry.remove(getFileKey(file))
    }

    operator fun get(file: VirtualFile): Managers? {
        return registry[getFileKey(file)]
    }

    private fun getFileKey(file: VirtualFile): String {
        return file.path
    }
}

data class Managers(
    val presenter: OpenedFilePresenter,
    val documentListener: DocumentListener,
)
