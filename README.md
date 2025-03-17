*Installation*

* git clone https://github.com/aaravnoronha/CountryList
* Open in Android Studio
* Build and run CountryList project (debug version)

*Overview*

This Android application showcases a country listing app built with Clean Architecture and the MVVM pattern. It leverages modern Android development practices, including Kotlin Coroutines, Flow, Hilt for dependency injection, and Room for offline caching.

The app fetches country data from a remote API, caches it locally, and displays it to users. It implements a robust data handling strategy with error management and loading states.

*Architecture*

Clean Architecture Layers

1. Presentation Layer
* UI: Activities and Fragments that display data to users
* ViewModel: Manages UI-related data, handles user interactions
* Components:
    * CountryViewModel: Manages country data state and UI logic
    * Uses ResourceState sealed class for handling loading, success, and error states
2. Domain Layer
* Contains business logic and interfaces
* Components:
    * Country: Domain model
    * CountryRepository: Interface defining data operations
    * GetCountriesUseCase: Business logic for fetching countries
3. Data Layer
* Handles data operations and mapping
* Components:
    * CountryRepositoryImpl: Implementation of repository interface
    * NetworkDataSource: Handles API calls
    * LocalDataSource: Manages local database operations
    * CountryMapper: Maps between DTO, Entity, and Domain models

Data Flow

* UI requests data through ViewModel
* ViewModel calls UseCase
* UseCase executes repository operations
* Data flows back through layers using Kotlin Flow
* Repository:
    * Attempts to fetch from network
    * Stores in local database
    * Falls back to cache if network fails

