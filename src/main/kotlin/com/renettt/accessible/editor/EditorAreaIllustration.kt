package com.renettt.accessible.editor

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * If conditions support it, makes a menu visible to display information about the caret.
 *
 * @see AnAction
 */
class EditorAreaIllustration : AnAction() {


    /**
     * Sets visibility and enables this action menu item if:
     * <ul>
     *   <li>a project is open</li>
     *   <li>an editor is active</li>
     * </ul>
     *
     * @param event Event related to this action
     */
    override fun update(event: AnActionEvent) {
        // Get required data keys
        val project: Project? = event.getProject()
        val editor: Editor? = event.getData(CommonDataKeys.EDITOR);
        // Set visibility only in case of existing project and editor
        event.presentation.isEnabledAndVisible = project != null && editor != null;
    }
    /**
     * Displays a message with information about the current caret.
     *
     * @param event Event related to this action
     */
    override fun actionPerformed(event: AnActionEvent) {
        // Get access to the editor and caret model. update() validated editor's existence.
        val editor: Editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel: CaretModel = editor.caretModel
        // Getting the primary caret ensures we get the correct one of a possible many.
        val primaryCaret: Caret = caretModel.primaryCaret
        // Get the caret information
        val logicalPos: LogicalPosition = primaryCaret.logicalPosition

        val visualPos: VisualPosition = primaryCaret.visualPosition
        val caretOffset = primaryCaret.offset;
        // Build and display the caret report.
        val report = "$logicalPos\n$visualPos\nOffset: $caretOffset"
        Messages.showInfoMessage(report, "Caret Parameters Inside The Editor");
    }

}

