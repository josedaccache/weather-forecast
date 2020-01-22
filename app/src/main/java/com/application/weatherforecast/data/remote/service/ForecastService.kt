package com.application.weatherforecast.data.remote.service

import com.application.weatherforecast.model.FiveDays
import com.application.weatherforecast.model.Forecast
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastService {

    @GET("data/2.5/weather")
    fun getForecast(
        @Query("APPID") apiKey: String,
        @Query("units") units: String,
        @Query("q") city: String
    ): Single<Forecast>

    @GET("data/2.5/forecast")
    fun getForecastByLocation(
        @Query("APPID") apiKey: String,
        @Query("units") units: String,
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): Single<FiveDays>
}