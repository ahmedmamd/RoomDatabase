package com.example.roomdatabase.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "login")
data class User(
    var email: String? = null,
    var password: String? = null,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}