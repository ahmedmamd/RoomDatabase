package com.example.roomdatabase.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Posts(
        var posts: String?=null
        ){
    @PrimaryKey(autoGenerate = true)
    var id:Int =0
}
