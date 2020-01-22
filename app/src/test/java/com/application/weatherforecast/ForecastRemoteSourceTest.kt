package com.application.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.application.weatherforecast.data.remote.service.ForecastService
import com.application.weatherforecast.data.remote.source.ForecastRemoteSource
import com.application.weatherforecast.domain.CityUseCase
import com.application.weatherforecast.model.*
import com.application.weatherforecast.presentation.viewmodel.CityViewModel
import com.application.weatherforecast.utils.APP_KEY
import com.application.weatherforecast.utils.METRIC
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

class ForecastRemoteSourceTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var service: ForecastService

    @InjectMocks
    var forecastRemoteSource = ForecastRemoteSource()

    private var testSingleForecast: Single<Forecast>? = null

    private var testSingleFiveDays: Single<FiveDays>? = null

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

        testSingleForecast = Single.just(forecast)
        Mockito.`when`(service.getForecast(APP_KEY, METRIC,"cityName")).thenReturn(testSingleForecast)
        val response = forecastRemoteSource.getForecast("cityName")

        Assert.assertEquals(testSingleForecast, response)

    }

    @Test
    fun getCityForecastFailure() {
        testSingleForecast = Single.error(Throwable())
        Mockito.`when`(service.getForecast(APP_KEY, METRIC,"cityName")).thenReturn(testSingleForecast)
        val response = forecastRemoteSource.getForecast("cityName")
        Assert.assertEquals(testSingleForecast, response)
    }

    @Test
    fun getCurrentCityForecastSuccess() {
        val main = Main(3.0, 1.0, 5.0)
        val wind = Wind(35.0, "deg")
        val weather = Weather("id", "main", "description", "icon")
        val weatherList = arrayListOf(weather)
        val forecast = Forecast(1L, main, wind, weatherList, "name")

        val main1 = Main(3.0, 1.0, 5.0)
        val wind1 = Wind(35.0, "deg")
        val weather1 = Weather("id", "main", "description", "icon")
        val weatherList1 = arrayListOf(weather1)
        val forecast1 = Forecast(1L, main1, wind1, weatherList1, "name")

        val forecastList = arrayListOf(forecast, forecast1)
        val city = City("id","name", "country")
        val fiveDays = FiveDays(forecastList, city)

        testSingleFiveDays = Single.just(fiveDays)
        Mockito.`when`(service.getForecastByLocation(APP_KEY, METRIC,"lat","long")).thenReturn(testSingleFiveDays)
        val response = forecastRemoteSource.getForecastByLocation("lat","long")

        Assert.assertEquals(testSingleFiveDays, response)

    }

    @Test
    fun getCurrentCityForecastFailure() {
        testSingleFiveDays = Single.error(Throwable())
        Mockito.`when`(service.getForecastByLocation(APP_KEY, METRIC,"lat","long")).thenReturn(testSingleFiveDays)
        val response = forecastRemoteSource.getForecastByLocation("lat","long")
        Assert.assertEquals(testSingleFiveDays, response)
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