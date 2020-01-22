package com.application.weatherforecast.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.weatherforecast.di.DaggerApiComponent
import com.application.weatherforecast.domain.CityUseCase
import com.application.weatherforecast.model.Forecast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CityViewModel : ViewModel() {

    @Inject
    lateinit var cityUseCase: CityUseCase

    private val disposable = CompositeDisposable()

    val forecast = MutableLiveData<ArrayList<Forecast>>()
    val forecastLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun fetchCityForecast(city: String) {
        loading.value = true
        disposable.add(
            cityUseCase.getCityForecast(city)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Forecast>() {
                    override fun onSuccess(value: Forecast?) {
                        if (forecast.value == null) {
                            forecast.value = ArrayList<Forecast>()
                        }

                        forecast.value?.add(value!!)
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

    fun cleatAllForecasts() {
        forecast.value = null
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}