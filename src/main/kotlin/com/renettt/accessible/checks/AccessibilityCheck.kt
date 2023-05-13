package com.renettt.accessible.checks

import java.util.*

interface AccessibilityCheck<T> {
    /**
     *  Регистрация данных о проверке в системе. todo: В будущем сделать автоматизированным.
     */
    val metaData: AccessibilityCheckMetaData

    /**
     * Основная логика проверки над элементом
     * @return list containing result information
     */
    fun runCheck(element: T): List<AccessibilityCheckResult>

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

    /**
     * A result that has been explicitly suppressed from throwing any Exceptions, used to allow for
     * known issues.
     */
    SUPPRESSED;
}
