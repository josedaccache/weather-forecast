package com.application.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.application.weatherforecast.domain.CurrentCityUseCase
import com.application.weatherforecast.model.*
import com.application.weatherforecast.presentation.viewmodel.CurrentCityViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.observers.TestObserver
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

class CurrentCityViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var currentCityUseCase: CurrentCityUseCase

    @InjectMocks
    var currentCityViewModel = CurrentCityViewModel()

    private var testSingle: Single<FiveDays>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun getCurrentCityForecastSuccess() {
        val main = Main(3.0, 1.0, 5.0)
        val wind = Wind(35.0, "deg")
        val weather = Weather("id", "main", "description", "icon")
        val weatherList = arrayListOf(weather)
        val forecast = Forecast(1L, main, wind, weatherList, "name")
        val forecastList = arrayListOf(forecast)
        val city = City("id","name", "country")
        val fiveDays = FiveDays(forecastList, city)

        testSingle = Single.just(fiveDays)
        Mockito.`when`(currentCityUseCase.getForecast("lat","long")).thenReturn(testSingle)
        currentCityViewModel.fetchForecast("lat","long")

        Assert.assertEquals(1, currentCityViewModel.forecast.value?.size)
        Assert.assertEquals(false, currentCityViewModel.loading.value)
        Assert.assertEquals(false, currentCityViewModel.forecastLoadError.value)
        //Test Response Data
        Assert.assertEquals(city.name + ", " + city.country, currentCityViewModel.cityName.value)
        Assert.assertEquals(main.temp, currentCityViewModel.forecast.value?.get(0)?.list?.get(0)?.main?.temp)
        Assert.assertEquals(main.tempMin, currentCityViewModel.forecast.value?.get(0)?.list?.get(0)?.main?.tempMin)
        Assert.assertEquals(main.tempMax, currentCityViewModel.forecast.value?.get(0)?.list?.get(0)?.main?.tempMax)
        Assert.assertEquals(wind.speed, currentCityViewModel.forecast.value?.get(0)?.list?.get(0)?.wind?.speed)
        Assert.assertEquals(
            weather.description,
            currentCityViewModel.forecast.value?.get(0)?.list?.get(0)?.weather?.get(0)?.description
        )

    }

    @Test
    fun getCurrentCityForecastFailure() {
        testSingle = Single.error(Throwable())
        Mockito.`when`(currentCityUseCase.getForecast("lat", "long")).thenReturn(testSingle)

        currentCityViewModel.fetchForecast("lat", "long")

        Assert.assertEquals(null, currentCityViewModel.forecast.value?.size)
        Assert.assertEquals(false, currentCityViewModel.loading.value)
        Assert.assertEquals(true, currentCityViewModel.forecastLoadError.value)
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
        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }

    }

}