package com.application.weatherforecast.domain

import com.application.weatherforecast.data.repository.CurrentCityRepository
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.model.Forecast
import io.reactivex.Single
import javax.inject.Inject

class CityUseCase {

    @Inject
    lateinit var repository: CurrentCityRepository

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getCityForecast(city: String): Single<Forecast> {
        return repository.getCityForecast(city)
    }
}