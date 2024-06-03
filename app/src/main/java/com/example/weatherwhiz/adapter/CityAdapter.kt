package com.example.weatherwhiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwhiz.R
import com.example.weatherwhiz.model.City

class CityAdapter(private val onCityClick: (City) -> Unit) :
    ListAdapter<City, CityAdapter.CityViewHolder>(CityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder  {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val previousItem = if (position > 0) getItem(position - 1) else null
        holder.bind(item, previousItem, onCityClick)
    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val headerTextView: TextView = itemView.findViewById(R.id.headerTextView)
        private val cityNameContainer: FrameLayout = itemView.findViewById(R.id.cityNameContainer)

        fun bind(city: City, previousCity: City?, onCityClick: (City) -> Unit) {
            cityNameTextView.text = city.city
            cityNameContainer.setOnClickListener {
                onCityClick(city)
            }

            val isHeaderVisible = previousCity == null || city.city.firstOrNull() != previousCity.city.firstOrNull()
            headerTextView.visibility = if (isHeaderVisible) View.VISIBLE else View.GONE
            headerTextView.text = city.city.firstOrNull()?.toString() ?: ""
        }
    }

    class CityDiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.city == newItem.city
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }
}
