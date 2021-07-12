package com.example.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdatabase.data.UserDao
import com.example.roomdatabase.pojo.Posts
import com.example.roomdatabase.pojo.User

@Database(entities = [User::class , Posts::class],version = 2)
 abstract class DatabaseClint : RoomDatabase() {
    abstract fun userDao(): UserDao?

companion object{
    var instance: DatabaseClint? = null
    var posts:DatabaseClint?=null

    @Synchronized
     fun getInstance(context: Context): DatabaseClint{
        if (instance == null) {
            instance = Room.databaseBuilder(context.applicationContext,DatabaseClint::class.java,"login").fallbackToDestructiveMigration()
                .build()
        }
        return instance as DatabaseClint
    }
    @Synchronized
    fun getPosts(context: Context):DatabaseClint{
        if (posts == null) {
            posts = Room.databaseBuilder(context.applicationContext,DatabaseClint::class.java,"posts").fallbackToDestructiveMigration()
                .build()
        }
        return posts as DatabaseClint
    }
}

}