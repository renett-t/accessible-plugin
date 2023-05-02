package com.renettt.accessible.configure

import com.renettt.accessible.checks.PsiAccessibilityChecksService
import com.renettt.accessible.di.DI
import com.renettt.accessible.presenter.OpenedFilesPresenter

class Configuration {

    fun configure() {
        configureDi()
    }
    private fun configureDi() {
        DI.single<PsiAccessibilityChecksService> {
            PsiAccessibilityChecksService()
        }

        DI.single {
            OpenedFilesPresenter()
        }
    }
}
