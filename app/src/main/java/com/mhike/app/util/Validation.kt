package com.mhike.app.util

import com.mhike.app.domain.model.Hike

fun validateHike(hike: Hike): ValidationResult {
    return when {
        hike.name.isBlank() ->
            ValidationResult(false, "Name is required")
        hike.location.isBlank() ->
            ValidationResult(false, "Location is required")
        hike.lengthKm <= 0 ->
            ValidationResult(false, "Length must be greater than zero")

        else ->
            ValidationResult(true)
    }
}
