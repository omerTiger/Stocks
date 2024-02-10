package com.example.stocks.domin

data class User(val id: String, val username: String, val fullName: String, val email: String,
           val image: String, val bio: String?) {
    constructor() : this("", "", "", "", "", "")


}