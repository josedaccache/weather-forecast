package com.application.weatherforecast.di

import com.application.weatherforecast.data.remote.source.ForecastRemoteSource
import com.application.weatherforecast.data.repository.CurrentCityRepository
import com.application.weatherforecast.domain.CityUseCase
import com.application.weatherforecast.domain.CurrentCityUseCase
import com.application.weatherforecast.presentation.viewmodel.CityViewModel
import com.application.weatherforecast.presentation.viewmodel.CurrentCityViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    fun inject(remoteSource: ForecastRemoteSource)

    fun inject(viewModel: CurrentCityViewModel)

    fun inject(repository: CurrentCityRepository)

    fun inject (currentCityUseCase: CurrentCityUseCase)

    fun inject (cityUseCase: CityUseCase)

    fun inject(viewModel: CityViewModel)

}