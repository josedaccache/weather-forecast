package com.application.weatherforecast.domain

import com.application.weatherforecast.data.repository.CurrentCityRepository
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.model.FiveDays
import io.reactivex.Single
import javax.inject.Inject

class CurrentCityUseCase {

    @Inject
    lateinit var repository: CurrentCityRepository

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getForecast(lat: String, long: String): Single<FiveDays> {
        return repository.getCurrentCityForecast(lat, long)
    }
}