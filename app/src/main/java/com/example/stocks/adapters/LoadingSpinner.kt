package com.example.stocks.adapters

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.example.stocks.R

class LoadingSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar

    init {
        LayoutInflater.from(context).inflate(R.layout.loading_spinner, this, true)
        progressBar = findViewById(R.id.loadingProgressBar)
    }

    fun showLoading() {
        progressBar.visibility = VISIBLE
    }

    fun hideLoading() {
        progressBar.visibility = GONE
    }
}
