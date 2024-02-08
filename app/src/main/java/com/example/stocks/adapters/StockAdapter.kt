package com.example.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.domin.Post
import com.example.stocks.domin.Stock

class StockAdapter(private val context: Context, var stocks: List<Stock>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockNameTextView: TextView = itemView.findViewById(R.id.stockNameTextView)
        val textViewOpen: TextView = itemView.findViewById(R.id.textViewOpen)
        val textViewClose: TextView = itemView.findViewById(R.id.textViewClose)
        val textViewHigh: TextView = itemView.findViewById(R.id.textViewHigh)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapter.StockViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockAdapter.StockViewHolder, position: Int) {
        val stock = stocks[position]

        holder.stockNameTextView.text = stock.name
        holder.textViewOpen.text = "Yesterday's Open: " + stock.open
        holder.textViewClose.text = "Yesterday's Close: " + stock.close
        holder.textViewHigh.text = "Yesterday's High: " + stock.high
        holder.itemView.tag = stocks[position].name

    }

    override fun getItemCount(): Int {
        return stocks.size
    }
}