// di/AppModule.kt
package com.example.countrylist.di

import android.app.Application
import androidx.room.Room
import com.example.countrylist.data.local.CountryDatabase
import com.example.countrylist.data.remote.CountryApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideCountryApi(retrofit: Retrofit): CountryApi {
        return retrofit.create(CountryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCountryDatabase(app: Application): CountryDatabase {
        return Room.databaseBuilder(
            app,
            CountryDatabase::class.java,
            "country_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCountryDao(database: CountryDatabase) = database.countryDao
}