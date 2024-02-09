package com.example.stocks.domin

data class Post(
    val id: String,
    val imageUrl: String,
    val caption: String,
    val userId: String?) {

    constructor() : this("", "", "", "")
}