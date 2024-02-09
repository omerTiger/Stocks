package com.example.stocks.domin

data class Stock(val name: String, val open: String, val close: String, val high: String) {
    constructor() : this("", "", "", "")
}