package com.application.weatherforecast.di

import com.application.weatherforecast.data.remote.service.ForecastService
import com.application.weatherforecast.data.remote.source.ForecastRemoteSource
import com.application.weatherforecast.data.repository.CurrentCityRepository
import com.application.weatherforecast.domain.CityUseCase
import com.application.weatherforecast.domain.CurrentCityUseCase
import com.application.weatherforecast.utils.BASE_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    @Provides
    fun provideForecastAPI(): ForecastService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ForecastService::class.java)
    }

    @Provides
    fun provideForecastModule(): ForecastRemoteSource {
        return ForecastRemoteSource()
    }

    @Provides
    fun provideCurrentCityUserCase() : CurrentCityUseCase {
        return CurrentCityUseCase()
    }

    @Provides
    fun provideCurrentCityRepository() : CurrentCityRepository {
        return CurrentCityRepository()
    }

    @Provides
    fun provideCityUserCase() : CityUseCase {
        return CityUseCase()
    }
}