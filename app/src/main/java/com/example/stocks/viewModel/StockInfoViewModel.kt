package com.example.stocks.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stocks.domin.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class StockInfoViewModel : ViewModel() {

    private val _stockInfoLiveData = MutableLiveData<List<Stock>>()
    val stocks: LiveData<List<Stock>> get() = _stockInfoLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val apiKey = "gQJLQXxt2VdR71EuTaEyDPepp52xdBS5"

    fun fetchStockInfo() {
        _isLoading.value = true

        GlobalScope.launch(Dispatchers.IO) {
            val stockInfo = fetchStockInfoFromApi()
            withContext(Dispatchers.Main) {
                _stockInfoLiveData.value = stockInfo
                _isLoading.value = false
            }
        }
    }

    private fun fetchStockInfoFromApi(): List<Stock> {
        val stocks = ArrayList<Stock>()
        val stockSymbol = ArrayList<String>()
        initSymbols(stockSymbol)

        for (symbol in stockSymbol) {
            try {
                val url =
                    URL("https://api.polygon.io/v2/aggs/ticker/$symbol/prev?unadjusted=true&apiKey=$apiKey")
                val urlConnection = url.openConnection() as HttpURLConnection
                val response = StringBuilder()
                buildResponse(BufferedReader(InputStreamReader(urlConnection.inputStream)), response)

                val results =
                    JSONObject(response.toString()).getJSONArray("results").get(0) as JSONObject
                val stock = Stock(
                    results.getString("T"),
                    results.getString("o"),
                    results.getString("c"),
                    results.getString("h")
                )

                stocks.add(stock)

            } catch (e: Exception) {
                e.printStackTrace()
                return stocks
            }
        }

        return stocks
    }

    private fun initSymbols(stockSymbol: ArrayList<String>) {
        stockSymbol.add("AAPL")
        stockSymbol.add("GOOG")
        stockSymbol.add("META")
        stockSymbol.add("DCFC")
        stockSymbol.add("AMD")
    }

    private fun buildResponse(bufferedReader: BufferedReader, response: StringBuilder) {
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            response.append(line)
        }
    }
}
