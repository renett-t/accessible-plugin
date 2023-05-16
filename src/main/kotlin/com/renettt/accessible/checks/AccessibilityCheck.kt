package com.renettt.accessible.checks

import java.util.*

interface AccessibilityCheck<T> {
    /**
     *  Регистрация данных о проверке в системе.
     */
    val metaData: AccessibilityCheckMetaData


    /**
     *  Проверяет, можно ли данный element допустить к проверке
     */
    fun availableFor(element: T): Boolean

    /**
     * Основная логика проверки над элементом с пред-обработкой. Обычно не нужно override-ить
     * todo: на будущее добавить результаты, показывающие
     *          - что с элементом всё ок
     *          - что элемент невалидный
     *    Сейчас возвращаются только ошибки
     * @return list containing result information
     */
    fun runCheck(element: T): List<AccessibilityCheckResult> {
        return if (availableFor(element))
            performCheckOperations(element)
        else emptyList()
    }

    /**
     * Основная логика проверки над элементом после пред-обработки.
     * @return list containing result information
     */
    fun performCheckOperations(element: T): List<AccessibilityCheckResult>

    /**
     * Converts check result metadata into human-readable representation.
     * @return локализованную строку описывающую результат проверки
     */
    fun getMessageForResultData(locale: Locale, resultId: Int, metadata: ResultMetadata?): String
}


data class AccessibilityCheckResult(
    val type: AccessibilityCheckResultType,
    val metadata: ResultMetadata?,
    val msg: String,
)


enum class AccessibilityCheckResultType {
    /** Clearly an accessibility bug, for example no speakable text on a clicked button  */
    ERROR,

    /**
     * Potentially an accessibility bug, for example finding another view with the same speakable
     * text as a clicked view
     */
    WARNING,

    /**
     * Information that may be helpful when evaluating accessibility, for example a listing of all
     * speakable text in a view hierarchy in the traversal order used by an accessibility service.
     */
    INFO,

    /**
     * Indication that a potential issue was identified, but it was resolved as not an accessibility
     * problem.
     */
    RESOLVED,

    /** A signal that the check was not run at all (ex. because the API level was too low)  */
    NOT_RUN,

    /** Indication that the check was not run at all because passed element is invalid (ex. missing
     *  important attribute, syntax errors)
     */
    ELEMENT_NOT_VALID,

    /**
     * A result that has been explicitly suppressed from throwing any Exceptions, used to allow for
     * known issues
     */
    SUPPRESSED,

    /** Indication that the check was run and element's accessibility state is considered acceptable
     */
    OK,
}
