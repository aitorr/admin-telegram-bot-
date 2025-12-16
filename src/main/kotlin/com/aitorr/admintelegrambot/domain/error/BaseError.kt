package com.aitorr.admintelegrambot.domain.error

/**
 * Base error interface for all domain errors
 * Provides a mechanism to chain errors and create a stacktrace-like representation
 */
interface BaseError {
    val message: String
    val sourceError: BaseError?
    
    /**
     * Generates a stacktrace-like representation of chained errors
     */
    fun toErrorTrace(): String {
        val errors = mutableListOf<String>()
        var current: BaseError? = this
        
        while (current != null) {
            errors.add("${current::class.simpleName}: ${current.message}")
            current = current.sourceError
        }
        
        return errors.joinToString("\n  Caused by: ", prefix = "")
    }
}
