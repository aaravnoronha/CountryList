// util/ResourceState.kt
package com.walmart.countrylist.util

sealed class ResourceState<out T> {
    data object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val error: String) : ResourceState<Nothing>()
}