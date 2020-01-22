package com.application.weatherforecast.presentation.ui.adapters

import android.content.res.Resources
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.weatherforecast.R
import com.application.weatherforecast.model.Forecast
import com.application.weatherforecast.utils.convertLongToTime
import com.application.weatherforecast.utils.getProgressDrawable
import com.application.weatherforecast.utils.loadImage
import kotlinx.android.synthetic.main.daily_forecast_layout.view.*

class DailyForecastAdapter(private var forecast: ArrayList<Forecast>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DailyForecastViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.daily_forecast_layout,
            parent, false
        )
    )

    override fun getItemCount() = forecast.size

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        holder.bind(forecast[position])
    }

    class DailyForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvDailyTime = view.tvDailyTime
        private var tvDailyTemp = view.tvDailyTemp
        private var tvDailyMinMaxTemp = view.tvDailyMinMaxTemp
        private var tvDailyWindSpeed = view.tvDailyWindSpeed
        private var tvDailyDescription = view.tvDailyDescription
        private var ivDailyIcon = view.ivDailyIcon

        private val progressDrawable = getProgressDrawable(view.context)
        var res: Resources = view.resources

        fun bind(forecast: Forecast) {
            tvDailyTime.text = convertLongToTime(forecast.time!!).replaceFirst("^0+(?!$)".toRegex(), "")
            tvDailyTemp.text = TextUtils.concat(
                forecast.main?.temp!!.toInt().toString(),
                res.getString(R.string.degree_celsius)
            )

            tvDailyMinMaxTemp.text = TextUtils.concat(
                forecast.main.tempMax.toInt().toString(),
                res.getString(R.string.degree_celsius),
                " / ",
                forecast.main.tempMin.toInt().toString(),
                res.getString(R.string.degree_celsius)
            )
            val speedInKm: Double = forecast.wind!!.speed * 3.6
            tvDailyWindSpeed.text = res.getString(R.string.wind_speed, "%.2f".format(speedInKm))

            var weatherDesc = ""
            if (forecast.weather?.size!! > 1) {
                forecast.weather.forEach { weather ->
                    weatherDesc += weather.description + ","
                }
            } else {
                weatherDesc = forecast.weather[0].description!!
            }
            tvDailyDescription.text = TextUtils.concat(weatherDesc)
            ivDailyIcon.loadImage(forecast.weather[0].icon, progressDrawable)
        }
    }
}