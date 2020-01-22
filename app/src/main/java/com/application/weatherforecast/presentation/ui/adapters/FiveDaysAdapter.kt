package com.application.weatherforecast.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.weatherforecast.R
import com.application.weatherforecast.model.CurrentCityForecast
import kotlinx.android.synthetic.main.five_days_layout.view.*

class FiveDaysAdapter(var fiveDaysMap: ArrayList<CurrentCityForecast>) :
    RecyclerView.Adapter<FiveDaysAdapter.FiveDaysViewHolder>() {

    fun updateFiveDays(newFiveDaysMap: ArrayList<CurrentCityForecast>) {
        fiveDaysMap.clear()
        fiveDaysMap.addAll(newFiveDaysMap)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FiveDaysViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.five_days_layout, parent, false)
    )

    override fun getItemCount() = fiveDaysMap.size

    override fun onBindViewHolder(holder: FiveDaysViewHolder, position: Int) {
        holder.bind(fiveDaysMap[position])
        holder.header.text = fiveDaysMap[position].time
    }

    class FiveDaysViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var rvDailyForecast = view.rvDailyForecast
        var header = view.header

        fun bind(forecast: CurrentCityForecast) {
            val dailyForecastAdapter = DailyForecastAdapter(forecast.list!!)
            rvDailyForecast.adapter = dailyForecastAdapter

        }
    }
}