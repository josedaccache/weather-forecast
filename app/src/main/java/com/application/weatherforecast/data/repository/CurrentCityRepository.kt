package com.application.weatherforecast.data.repository

import com.application.weatherforecast.data.remote.source.ForecastRemoteSource
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.model.FiveDays
import com.application.weatherforecast.model.Forecast
import io.reactivex.Single
import javax.inject.Inject

class CurrentCityRepository {

    @Inject
    lateinit var forecastRemoteSource: ForecastRemoteSource
    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getCurrentCityForecast (lat: String, long: String) : Single<FiveDays> {
       return forecastRemoteSource.getForecastByLocation(lat, long)
    }

    fun getCityForecast(city: String): Single<Forecast> {
        return forecastRemoteSource.getForecast(city)
    }

}