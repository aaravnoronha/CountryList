package com.example.countrylist.domain.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
    data class Loading<T>(val isLoading: Boolean = true) : Result<T>()
}