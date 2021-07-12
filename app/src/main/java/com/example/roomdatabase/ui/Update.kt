package com.example.roomdatabase.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.roomdatabase.DatabaseClint
import com.example.roomdatabase.R
import com.example.roomdatabase.databinding.ActivityUpdateBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class Update : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding
    var database : DatabaseClint?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update)

        binding.btUpdate.setOnClickListener(View.OnClickListener {


        })
    }
}