package com.example.stocks.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CachedPost)

    @Update
    suspend fun updatePost(post: CachedPost)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<CachedPost>

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("UPDATE posts SET imageUrl = :updatedImageUrl, caption = :updatedCaption WHERE id = :postId")
    suspend fun updatePostById(postId: String, updatedImageUrl: String, updatedCaption: String)
}
