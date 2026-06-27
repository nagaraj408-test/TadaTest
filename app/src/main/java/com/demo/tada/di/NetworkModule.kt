package com.demo.tada.di

import com.demo.tada.data.remote.api.AirQualityApi
import com.demo.tada.data.remote.api.BookingApi
import com.demo.tada.data.remote.api.ReverseGeocodeApi
import com.demo.tada.data.remote.mock.MockBookingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMockBookingInterceptor(): MockBookingInterceptor {
        return MockBookingInterceptor()
    }

    @Provides
    @Singleton
    @Named("real")
    fun provideRealOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("mock")
    fun provideMockOkHttpClient(
        mockBookingInterceptor: MockBookingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(mockBookingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("aqi")
    fun provideAqiRetrofit(
        @Named("real") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("geo")
    fun provideGeoRetrofit(
        @Named("real") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.bigdatacloud.net/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("booking")
    fun provideBookingRetrofit(
        @Named("mock") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mock.booking/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideAirQualityApi(
        @Named("aqi") retrofit: Retrofit
    ): AirQualityApi {
        return retrofit.create(
            AirQualityApi::class.java
        )
    }

    @Provides
    @Singleton
    fun provideReverseGeocodeApi(
        @Named("geo") retrofit: Retrofit
    ): ReverseGeocodeApi {
        return retrofit.create(
            ReverseGeocodeApi::class.java
        )
    }

    @Provides
    @Singleton
    fun provideBookingApi(@Named("booking") retrofit: Retrofit): BookingApi {
        return retrofit.create(BookingApi::class.java)
    }
}