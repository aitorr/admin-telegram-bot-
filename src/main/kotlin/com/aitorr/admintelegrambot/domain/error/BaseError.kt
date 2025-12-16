package com.aitorr.admintelegrambot.domain.error

interface BaseError {
    val message: String
    val sourceError: BaseError?
    
    fun toErrorTrace(): String {
        val errors = mutableListOf<String>()
        var current: BaseError? = this
        
        while (current != null) {
            errors.add("${current::class.simpleName}: ${current.message}")
            current = current.sourceError
        }
        
        return errors.joinToString(separator = "\n  Caused by: ")
    }
}
