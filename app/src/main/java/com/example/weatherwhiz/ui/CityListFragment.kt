package com.example.weatherwhiz.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherwhiz.R
import com.example.weatherwhiz.adapter.CityAdapter
import com.example.weatherwhiz.databinding.FragmentCityListBinding
import com.example.weatherwhiz.model.City
import com.example.weatherwhiz.util.StickyHeaderDecoration
import com.example.weatherwhiz.viewModel.CityListViewModel

class CityListFragment : Fragment() {
    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CityListViewModel
    private lateinit var adapter: CityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CityListViewModel::class.java]
        setupRecyclerView()
        setupSearch()
        setupRetryButton()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.cityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CityAdapter { city ->
            navigateToWeatherFragment(city)
        }
        binding.cityRecyclerView.adapter = adapter
        binding.cityRecyclerView.addItemDecoration(StickyHeaderDecoration(adapter))
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCities(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterCities(query: String) {
        val filteredList = if (query.isEmpty()) {
            viewModel.cities.value ?: listOf()
        } else {
            viewModel.cities.value?.filter {
                it.city.startsWith(query, ignoreCase = true)
            } ?: listOf()
        }
        adapter.submitList(filteredList)
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            retryFetchCities()
        }
    }

    private fun observeViewModel() {
        viewModel.cities.observe(viewLifecycleOwner) { cities ->
            adapter.submitList(cities)
            binding.progressBar.visibility = if (cities.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.errorTextView.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            binding.cityRecyclerView.visibility = View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error) showError() else hideError()
        }
    }

    private fun showError() {
        binding.cityRecyclerView.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchEditText.visibility = View.GONE
    }

    private fun hideError() {
        binding.errorTextView.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.cityRecyclerView.visibility = View.VISIBLE
        binding.searchEditText.visibility = View.VISIBLE
    }

    private fun retryFetchCities() {
        _binding?.let {
            it.progressBar.visibility = View.VISIBLE
            viewModel.fetchCities()
        }
    }

    private fun navigateToWeatherFragment(city: City) {
        val fragment = WeatherFragment.newInstance(city.city, city.latitude, city.longitude)
        parentFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}