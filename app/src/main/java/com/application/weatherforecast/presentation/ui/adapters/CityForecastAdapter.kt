package com.application.weatherforecast.presentation.ui.adapters

import android.content.res.Resources
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.weatherforecast.R
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.utils.getProgressDrawable
import com.application.weatherforecast.utils.loadImage
import kotlinx.android.synthetic.main.city_forecast_layout.view.*


class CityForecastAdapter(private var forecast: ArrayList<Forecast>) :
    RecyclerView.Adapter<CityForecastAdapter.CityForecastViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CityForecastViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.city_forecast_layout,
            parent, false
        )
    )

    override fun getItemCount() = forecast.size

    override fun onBindViewHolder(holder: CityForecastViewHolder, position: Int) {
        holder.bind(forecast[position])
    }

    class CityForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvCityTemp = view.tvCityTemp
        private var tvCityMinMaxTemp = view.tvCityMinMaxTemp
        private var tvCityWindSpeed = view.tvCityWindSpeed
        private var tvCityDescription = view.tvCityDescription
        private var tvCityName = view.tvCityName
        private var ivForecast = view.ivForecast

        private val progressDrawable = getProgressDrawable(view.context)
        var res: Resources = view.resources

        fun bind(forecast: Forecast) {
            tvCityName.text = forecast.name
            tvCityTemp.text = TextUtils.concat(forecast.main?.temp!!.toInt().toString(), res.getString(R.string.degree_celsius))
            tvCityMinMaxTemp.text = TextUtils.concat(
                forecast.main.tempMax.toInt().toString(), res.getString(R.string.degree_celsius), " / ",
                forecast.main.tempMin.toInt().toString(), res.getString(R.string.degree_celsius)
            )
            val speedInKm: Double = forecast.wind!!.speed * 3.6
            tvCityWindSpeed.text = res.getString(R.string.wind_speed, "%.2f".format(speedInKm))
            var weatherDesc = ""
            if (forecast.weather?.size!! > 1) {
                forecast.weather.forEach { weather ->
                    weatherDesc += weather.description + ","
                }
            } else {
                weatherDesc = forecast.weather[0].description!!
            }
            tvCityDescription.text = TextUtils.concat(weatherDesc)
            ivForecast.loadImage(forecast.weather[0].icon, progressDrawable)

        }
    }
}