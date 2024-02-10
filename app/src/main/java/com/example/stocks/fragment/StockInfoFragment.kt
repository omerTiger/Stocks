package com.example.stocks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.adapters.LoadingSpinner
import com.example.stocks.adapters.StockAdapter
import com.example.stocks.viewModel.StockInfoViewModel

class StockInfoFragment : Fragment() {

    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var stockAdapter: StockAdapter
    private lateinit var stockInfoViewModel: StockInfoViewModel
    private lateinit var loadingSpinner: LoadingSpinner


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_stock_info, container, false)
        stockInfoViewModel = ViewModelProvider(this)[StockInfoViewModel::class.java]

        stockRecyclerView = view.findViewById(R.id.stockRecyclerView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        stockAdapter = StockAdapter(requireContext(), listOf())
        stockRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        stockRecyclerView.adapter = stockAdapter

        stockInfoViewModel.stocks.observe(viewLifecycleOwner) { stocks ->
            stockAdapter.stocks = stocks
            stockAdapter.notifyDataSetChanged()
        }

        stockInfoViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingSpinner.showLoading()
            } else {
                loadingSpinner.hideLoading()
            }
        }

        stockInfoViewModel.fetchStockInfo()

        return view
    }
}