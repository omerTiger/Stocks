package com.example.stocks.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class CachedPost(
    @PrimaryKey val id: String,
    val imageUrl: String,
    val caption: String,
    val userId: String?
)