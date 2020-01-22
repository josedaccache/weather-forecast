package com.application.weatherforecast.data.remote.source

import com.application.weatherforecast.data.remote.service.ForecastService
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.model.FiveDays
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.utils.APP_KEY
import com.application.weatherforecast.utils.METRIC
import io.reactivex.Single
import javax.inject.Inject

class ForecastRemoteSource {

    @Inject
    lateinit var service: ForecastService

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getForecast(city: String): Single<Forecast> {
        return service.getForecast(APP_KEY, METRIC, city)
    }

    fun getForecastByLocation(lat: String, long: String): Single<FiveDays> {
        return service.getForecastByLocation(APP_KEY, METRIC, lat, long)
    }

}