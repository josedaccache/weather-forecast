package com.application.weatherforecast.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.application.weatherforecast.R
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.presentation.ui.adapters.CityForecastAdapter
import com.application.weatherforecast.presentation.viewmodel.CityViewModel
import com.application.weatherforecast.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_city_forecast.*

class CityForecastFragment : Fragment() {

    lateinit var viewModel: CityViewModel
    lateinit var cityForecastAdapter: CityForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_city_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CityViewModel::class.java)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        btnGetForecast.setOnClickListener {
            val enteredText = edInput.editableText.toString()
            val count = enteredText.count { ",".contains(it) }
            when {
                count < 2 -> {
                    textInputLayout.error = getString(R.string.less_then_three)
                }
                count > 6 -> {
                    textInputLayout.error = getString(R.string.more_then_seven)
                }
                else -> {
                    val enteredTextArray = enteredText.toLowerCase().split(",").toTypedArray()
                    val altArray = arrayListOf<String>()
                    enteredTextArray.forEach { city ->
                        if (city.trim().isNotEmpty())
                            altArray.add(city.trim())
                    }
                    val arrayCount = altArray.distinct().size
                    when {
                        arrayCount < 3 -> textInputLayout.error =
                            getString(R.string.less_then_three_distinct)
                        arrayCount > 7 -> textInputLayout.error =
                            getString(R.string.more_then_seven_distinct)
                        else -> {
                            textInputLayout.error = null
                            fetchWeather(altArray)
                        }
                    }
                    edInput.hideKeyboard()
                }
            }
        }
    }

    private fun fetchWeather(citiesArray: ArrayList<String>) {
        viewModel.cleatAllForecasts()
        citiesArray.distinct().forEach { city ->
            viewModel.fetchCityForecast(city)
        }
    }

    private fun observeViewModel() {
        viewModel.forecast.observe(this, Observer { forecast ->
            forecast?.let {
                rlCityWeather.visibility = View.VISIBLE
                rvCityWeather.visibility = View.VISIBLE
                setupWeatherList(forecast)
            }
        })
        viewModel.forecastLoadError.observe(this, Observer { isError ->
            isError?.let {
                rlCityWeather.visibility = View.VISIBLE
                tvListError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                pbLoadingView.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    rlCityWeather.visibility = View.VISIBLE
                    rvCityWeather.visibility = View.GONE
                }
            }
        })
    }

    private fun setupWeatherList(forecast: ArrayList<Forecast>) {
        cityForecastAdapter = CityForecastAdapter(forecast)
        rvCityWeather.adapter = cityForecastAdapter

    }

}