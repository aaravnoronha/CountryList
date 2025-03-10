// di/RepositoryModule.kt
package com.example.countrylist.di

import com.example.countrylist.data.local.LocalDataSource
import com.example.countrylist.data.local.LocalDataSourceImpl
import com.example.countrylist.data.remote.NetworkDataSource
import com.example.countrylist.data.remote.NetworkDataSourceImpl
import com.example.countrylist.data.repository.CountryRepositoryImpl
import com.example.countrylist.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNetworkDataSource(
        networkDataSourceImpl: NetworkDataSourceImpl
    ): NetworkDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): LocalDataSource

    @Binds
    @Singleton
    abstract fun bindCountryRepository(
        repositoryImpl: CountryRepositoryImpl
    ): CountryRepository
}