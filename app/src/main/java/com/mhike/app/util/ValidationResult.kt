package com.mhike.app.util


data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
