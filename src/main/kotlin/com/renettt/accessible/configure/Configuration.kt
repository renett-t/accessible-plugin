package com.renettt.accessible.configure

import com.renettt.accessible.di.injector.AccessibleInjector
import com.renettt.accessible.di.injector.AccessibleInjectorImpl

object Configuration {
    private val INSTANCE = AccessibleInjectorImpl()
    fun injector() = INSTANCE
    operator fun invoke(): AccessibleInjector = INSTANCE


    private fun configureDi() {
//        DI.single<PsiAccessibilityChecksService> {
//            PsiAccessibilityChecksService()
//        }

//        DI.single {
//            OpenedFilesPresenter()
//        }
    }

}
