package com.example.weatherwhiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherwhiz.api.NetworkService
import com.example.weatherwhiz.databinding.FragmentWeatherBinding
import com.example.weatherwhiz.model.WeatherResponse
import com.example.weatherwhiz.viewModel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_CITY_NAME = "city_name"
private const val ARG_LATITUDE = "latitude"
private const val ARG_LONGITUDE = "longitude"

class WeatherFragment : Fragment() {
    private var cityName: String? = null
    private var latitude: String? = null
    private var longitude: String? = null

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cityName = it.getString(ARG_CITY_NAME)
            latitude = it.getString(ARG_LATITUDE)
            longitude = it.getString(ARG_LONGITUDE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        observeViewModel()
        fetchWeather()
        binding.btnRetry.setOnClickListener {
            fetchWeather()
        }

        return binding.root
    }

    private fun fetchWeather() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                latitude?.let { lat ->
                    longitude?.let { lon ->
                        _binding?.let { binding ->
                            binding.progressBar.visibility = View.VISIBLE
                            val response = withContext(Dispatchers.IO) {
                                NetworkService.weatherApi.getWeather(lat.toDouble(), lon.toDouble())
                            }
                            binding.progressBar.visibility = View.GONE
                            updateUI(response)
                        }
                    }
                }
            } catch (e: Exception) {
                _binding?.let { binding ->
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                updateUI(weather)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error) {
                binding.tempTextView.visibility = View.GONE
                binding.cityTextView.visibility = View.GONE
                val lat = arguments?.getDouble("lat")
                val lon = arguments?.getDouble("lon")
                if (lat != null && lon != null) {
                    viewModel.fetchWeather(lat, lon)
                }
            }
        }
    }

    private fun updateUI(weather: WeatherResponse) {
        binding.tempTextView.text = "${Math.round(weather.main.temp)}°C"
        binding.cityTextView.text = cityName //weather.name
        binding.tempTextView.visibility = View.VISIBLE
        binding.cityTextView.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(cityName: String, latitude: String, longitude: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CITY_NAME, cityName)
                    putString(ARG_LATITUDE, latitude)
                    putString(ARG_LONGITUDE, longitude)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}