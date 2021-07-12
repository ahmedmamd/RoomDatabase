package com.example.roomdatabase.data

import androidx.room.*
import com.example.roomdatabase.pojo.Posts
import com.example.roomdatabase.pojo.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


@Dao
interface UserDao {
    @Query("SELECT * FROM login")
    fun getAll(): Single<List<User?>?>?

    @Insert
    fun insert(user: User?): Completable?

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Single<List<Posts?>?>?

    @Insert
    fun insertpost(post: Posts?): Completable?

    @Delete
    fun deletePost(post: Posts?): Completable?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePost(post: Posts): Completable?

}
