package com.renettt.accessible.utils.notused

import com.intellij.codeHighlighting.Pass.LINE_MARKERS
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.lang.xml.XMLLanguage
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pass
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.JBColor
import icons.Icons
import java.awt.Color
import java.awt.Font
import javax.swing.Icon


/**
 *
 * docs reference:
 * - https://github.com/JetBrains/intellij-community/blob/idea/231.8109.175/platform/platform-api/src/com/intellij/ui/EditorNotifications.java
 * - https://github.com/nazmulidris/idea-plugin-example2/blob/main/docs/linemarkerprovider.md
 */
class MyXmlTagNotificationProvider : LineMarkerProvider {

    //    companion object {
//        private const val GROUP_DISPLAY_ID = "MyXmlTagNotification"
//        private val GROUP_DISPLAY_NAME = "MyXmlTagNotification"
//        private val GROUP = NotificationGroup(GROUP_DISPLAY_ID, NotificationDisplayType.STICKY_BALLOON, true)
//        private val KEY = com.intellij.openapi.editor.colors.EditorColorsManager.TOPIC
//            .createUniqueVariantKey("MyXmlTagNotificationProvider")
//    }
//
//    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
//        // Check if the element is an XML tag
//        if (element is XmlTag) {
//            val icon: Icon = Icons.defaultIcon
//
//            // todo: WTF
//            val gutterIconRenderer: GutterIconRenderer =
//                MyGutterIconRenderer(icon, "This is a custom message", "action1", "acion2")
//
//            return LineMarkerInfo(element, element.getTextRange())
//        }
//
//        return null
//    }
//
//
//    class MyNotificationPanel(private val tag: XmlTag) :
//        EditorNotificationPanel() {
//        private val font = Font("Arial", Font.BOLD, 12)
//        private val textColor = JBColor(Color.BLACK, JBColor.RED.darker())
//
//
//        override fun getText() = "Notification message for tag '${tag.name}'"
//
//        fun getIcon(): Icon = tag.getIcon(0)
//
//        fun createGutterIconRenderer() = MyGutterIconRenderer(
//            getIcon(),
//            "message",
//            "action1",
//            "action2"
//        )
//
//        // GutterIconNavigationHandler
//
//    }
//
////    override fun apply(editor: Editor, file: PsiFile) {
////        val psiManager = PsiManager.getInstance(editor.project)
////        val currentTag = psiManager.findFile(file.virtualFile)
////            ?.findElementAt(editor.caretModel.offset)?.parent?.parent as? XmlTag
////        if (currentTag == tag) {
////            EditorColorsManager.getInstance().globalScheme.editorFontName
////            editor.scrollingModel.scrollToCaret(0)
////        }
////    }
//
//    //
//    //        val panel = JPanel()
//    //        val textAttributes = TextAttributes(textColor, null, null, EffectType.WAVE_UNDERSCORE, 0)
//    //        panel.foreground = JBColor.BLACK
//    //        panel.background = JBColor.WHITE
//    //        panel.font = font
//    //        panel.add(JLabel(getText(), JLabel.CENTER).apply {
//    //            setForeground(textColor)
//    //            setFont(font)
//    //        })
//    //        panel.addMouseListener(object : MouseAdapter() {
//    //            override fun mouseClicked(e: MouseEvent) {
//    //                // Navigate to the tag in the editor when clicked
//    //                val file = tag.containingFile
//    //                val virtualFile = file.virtualFile
//    //                if (virtualFile != null) {
//    //                    val editor = FileEditorManager.getInstance(tag.project).selectedTextEditor
//    //                    if (editor != null && virtualFile == PsiUtilCore.getVirtualFile(file)) {
//    //                        val element = tag.navigationElement
//    //                        val offset = element.textOffset
//    //                        val length = element.textLength
//    //                        editor.caretModel.moveToOffset(offset)
//    //                        editor.selectionModel.setSelection(offset, offset + length)
//    //                        editor.scrollingModel.scrollToCaret(0)
//    //                    }
//    //                }
//    //            }
//    //
//    //            override fun mouseEntered(e: MouseEvent) {
//    //                panel.foreground = textColor
//    //            }
//    //
//    //            override fun mouseExited(e: MouseEvent) {
//    //                panel.foreground = JBColor.BLACK
//    //            }
//    //        })
//    //        return panel
//    override fun getContextMenuGroupId() = GROUP_DISPLAY_ID
//    override fun getKey() = KEY
//
//    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor): MyNotificationPanel? {
//        if (file.language != XMLLanguage.INSTANCE) {
//            return null
//        }
//
//        val psiManager = PsiManager.getInstance(file.project)
//        val tag = psiManager.findFile(file.virtualFile)
//            ?.findElementAt(fileEditor.caretModel.offset)?.parent?.parent as? XmlTag
//        if (tag != null && tag.name == "myTag") {
//            return MyNotificationPanel(tag)
//        }
//        return null
//
//
//    }
//
//    override fun createNotificationPanel(
//        file: VirtualFile,
//        fileEditor: FileEditor,
//        project: Project
//    ): MyNotificationPanel? {
//        return super.createNotificationPanel(file, fileEditor, project)
//    }
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*> {
        TODO("Not yet implemented")
    }


}
