package com.application.weatherforecast.model

import com.google.gson.annotations.SerializedName


data class FiveDays(
    @SerializedName("list")
    val list: List<Forecast>?,
    @SerializedName("city")
    val city: City?
)


data class CurrentCityForecast(
    val time: String?,
    val list: ArrayList<Forecast>?
)

data class Forecast(
    @SerializedName("dt")
    val time: Long?,
    @SerializedName("main")
    val main: Main?,
    @SerializedName("wind")
    val wind: Wind?,
    @SerializedName("weather")
    val weather: List<Weather>?,
    @SerializedName("name")
    val name: String = ""
)

data class Main(
    @SerializedName("temp")
    val temp: Double = 0.0,
    @SerializedName("temp_min")
    val tempMin: Double = 0.0,
    @SerializedName("temp_max")
    val tempMax: Double = 0.0
)

data class Wind(
    @SerializedName("speed")
    val speed: Double = 0.0,
    @SerializedName("deg")
    val deg: String?
)

data class Weather(
    @SerializedName("id")
    val id: String?,
    @SerializedName("main")
    val main: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("icon")
    val icon: String?
)

data class City (
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("country")
    val country: String?

)