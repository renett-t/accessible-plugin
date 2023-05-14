package com.renettt.accessible.utils.notused.wizard

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class DemoModuleWizardStep : ModuleBuilder() {
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {

    }
    override fun createWizardSteps(
        wizardContext: WizardContext,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
    return arrayOf(object : ModuleWizardStep() {
        override fun getComponent(): JComponent {
            return JLabel("Put your content here baaabe")
        }

        override fun updateDataModel() {}
    })
    }


    override fun getModuleType(): ModuleType<*> {
        return ModuleType.EMPTY
    }
}
