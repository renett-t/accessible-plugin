package com.renettt.accessible.presenter


/**
 * Генератор уникальных идентификаторов для MessageAnnotationDialog.
 * Идентификаторы нужны, прежде всего, чтобы не рисовать диалог заново, если он уже отображен в данный момент.
 */
class MessageAnnotationDialogIdsGenerator(private val start: Int) {
    // по-хорошему добавить синхронизаций
    private var counter = start

    fun next(): Int = counter++

    fun restart(): Unit {
        counter = start
    }
}
