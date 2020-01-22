package com.application.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.application.weatherforecast.domain.CityUseCase
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.model.Main
import com.application.weatherforecast.model.Weather
import com.application.weatherforecast.model.Wind
import com.application.weatherforecast.presentation.viewmodel.CityViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class CityViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var cityUseCase: CityUseCase

    @InjectMocks
    var cityViewModel = CityViewModel()

    private var testSingle: Single<Forecast>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun getCityForecastSuccess() {
        val main = Main(3.0, 1.0, 5.0)
        val wind = Wind(35.0, "deg")
        val weather = Weather("id", "main", "description", "icon")
        val weatherList = arrayListOf(weather)
        val forecast = Forecast(1L, main, wind, weatherList, "name")

        testSingle = Single.just(forecast)
        Mockito.`when`(cityUseCase.getCityForecast("cityName")).thenReturn(testSingle)
        cityViewModel.fetchCityForecast("cityName")

        Assert.assertEquals(1, cityViewModel.forecast.value?.size)
        Assert.assertEquals(false, cityViewModel.loading.value)
        Assert.assertEquals(false, cityViewModel.forecastLoadError.value)
        //Test Response Data
        Assert.assertEquals(main.temp, cityViewModel.forecast.value?.get(0)?.main?.temp)
        Assert.assertEquals(main.tempMin, cityViewModel.forecast.value?.get(0)?.main?.tempMin)
        Assert.assertEquals(main.tempMax, cityViewModel.forecast.value?.get(0)?.main?.tempMax)
        Assert.assertEquals(wind.speed, cityViewModel.forecast.value?.get(0)?.wind?.speed)
        Assert.assertEquals(weather.description, cityViewModel.forecast.value?.get(0)?.weather?.get(0)?.description)

    }

    @Test
    fun getCityForecastFailure() {

        testSingle = Single.error(Throwable())
        Mockito.`when`(cityUseCase.getCityForecast("cityName")).thenReturn(testSingle)

        cityViewModel.fetchCityForecast("cityName")

        Assert.assertEquals(null, cityViewModel.forecast.value?.size)
        Assert.assertEquals(false, cityViewModel.loading.value)
        Assert.assertEquals(true, cityViewModel.forecastLoadError.value)
    }


    @Before
    fun setUpRxSchedulers() {
        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }
        RxJavaPlugins.reset()
        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }

    }

}