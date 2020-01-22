package com.application.weatherforecast.presentation.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.domain.CurrentCityUseCase
import com.application.weatherforecast.model.CurrentCityForecast
import com.application.weatherforecast.model.FiveDays
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.utils.convertLongToDate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CurrentCityViewModel : ViewModel() {

    @Inject
    lateinit var currentCityUseCase: CurrentCityUseCase

    private val disposable = CompositeDisposable()

    val forecast = MutableLiveData<List<CurrentCityForecast>>()
    val forecastLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val cityName = MutableLiveData<String> ()

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun fetchForecast(latitude: String, longitude: String) {
        loading.value = true
        disposable.add(
            currentCityUseCase.getForecast(latitude, longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<FiveDays>() {
                    override fun onSuccess(value: FiveDays?) {
                        forecast.value = value?.let { parseResponse(it) }
                        cityName.value = value?.city?.name + ", " + value?.city?.country
                        forecastLoadError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        forecastLoadError.value = true
                        loading.value = false
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    private fun parseResponse(response: FiveDays): ArrayList<CurrentCityForecast> {
        val currentCityForecast = ArrayList<CurrentCityForecast>()
        response.list?.forEach { forecast ->
            if (currentCityForecast.any { it.time!! == convertLongToDate(forecast.time!!) }) {
                currentCityForecast.filter { it.time!! == convertLongToDate(forecast.time!!) }[0].list?.add(
                    forecast
                )
            } else {
                val list = ArrayList<Forecast>()
                list.add(forecast)
                val currentForecast = CurrentCityForecast(convertLongToDate(forecast.time!!), list)
                currentCityForecast.add(currentForecast)
            }

        }
        return currentCityForecast
    }

}