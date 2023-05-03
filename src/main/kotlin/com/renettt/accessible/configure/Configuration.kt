package com.renettt.accessible.configure

import com.renettt.accessible.checks.PsiAccessibilityChecksService
import com.renettt.accessible.di.DI
import com.renettt.accessible.di.injector.AccessibleInjector
import com.renettt.accessible.di.injector.AccessibleInjectorImpl
import com.renettt.accessible.presenter.OpenedFilesPresenter

object Configuration {
    private val INSTANCE = AccessibleInjectorImpl()
    fun injector() = INSTANCE
    operator fun invoke(): AccessibleInjector = INSTANCE


    private fun configureDi() {
        DI.single<PsiAccessibilityChecksService> {
            PsiAccessibilityChecksService()
        }

//        DI.single {
//            OpenedFilesPresenter()
//        }
    }

}
