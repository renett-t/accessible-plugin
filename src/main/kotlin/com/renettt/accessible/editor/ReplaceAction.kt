package com.renettt.accessible.editor

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


/**
 * Menu action to replace a selection of characters with a fixed string.
 *
 * @see AnAction
 */
class ReplaceAction : AnAction() {
    /**
     * Replaces the run of text selected by the primary caret with a fixed string.
     *
     * @param e Event related to this action
     */
    override fun actionPerformed(event: AnActionEvent) {
        // Get all the required data from data keys
        // Editor and Project were verified in update(), so they are not null.
        val editor: Editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)

        val document: Document = editor.document

        // Work off of the primary caret to get the selection info
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            document.replaceString(start, end, "editor_basics")
        }
        // De-select the text range that was just replaced
        primaryCaret.removeSelection()
    }

    /**
     * Sets visibility and enables this action menu item if:
     *
     *  * a project is open
     *  * an editor is active
     *  * some characters are selected
     *
     *
     * @param e Event related to this action
     */
    override fun update(@NotNull e: AnActionEvent) {
        // Get required data keys
        val project: Project? = e.project
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        // Set visibility and enable only in case of existing project and editor and if a selection exists
        e.presentation.isEnabledAndVisible =
            project != null && editor != null && editor.selectionModel.hasSelection()
    }
}
