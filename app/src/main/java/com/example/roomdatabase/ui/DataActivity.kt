package com.example.roomdatabase.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.DatabaseClint
import com.example.roomdatabase.R
import com.example.roomdatabase.adapter.PostsAdapter
import com.example.roomdatabase.databinding.ActivityDataBinding
import com.example.roomdatabase.pojo.Posts
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class DataActivity : AppCompatActivity(),PostsAdapter.Itemclicked {
    lateinit var binding:ActivityDataBinding

    val mutablePosts  = ArrayList<Posts?>()
    val adapter = PostsAdapter(mutablePosts,this,this)
    var database : DatabaseClint ?=null

    var getPostMutableLiveData = MutableLiveData<List<Posts?>>()
    fun getPostsLiveData(): LiveData<List<Posts?>>?{
        return getPostMutableLiveData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data)
        database =  DatabaseClint.getPosts(this)
        setUPui()
        setUpObserver()
    }

        private fun setUpObserver() {
            adapter.deletePostsLiveData()?.observe(this, androidx.lifecycle.Observer {
                mutablePosts.clear()
                getposts()
            })
            getPostsLiveData()?.observe(this, androidx.lifecycle.Observer { posts ->
//                mutablePosts.clear()
                  mutablePosts.addAll(posts)
                  adapter.notifyDataSetChanged()
            })
        }

    private fun setUPui() {
        getposts()
        binding.rvPostsList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
      //   adapter = PostsAdapter(mutablePosts)
        binding.rvPostsList.adapter =adapter
            binding.addPost.setOnClickListener(View.OnClickListener {
                database?.userDao()?.insertpost(Posts(binding.post.text.toString()))!!
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : CompletableObserver {
                            override fun onSubscribe(d: Disposable?) {
                            }
                            override fun onComplete() {
                                mutablePosts.clear()
                                getposts()
                               // getPostMutableLiveData.postValue(Posts(binding.post.text.toString()))
                                Log.e("addPost", "post added successfully "+Posts(binding.post.text.toString()))
                                Toast.makeText(this@DataActivity, "register done", Toast.LENGTH_SHORT).show()
                            }
                            override fun onError(e: Throwable?) {
                            }
                        })
            })
    }
    fun getposts(){
        database?.userDao()?.getAllPosts()?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : SingleObserver<List<Posts?>?> {
                    override fun onSubscribe(d: Disposable?) {
                    }
                    override fun onError(e: Throwable?) {
                    }

                    override fun onSuccess(posts: List<Posts?>?) {
                        getPostMutableLiveData.postValue(posts)
                    }
                })
    }

    override fun itemChoose(position: Int) {
//        mutablePosts.clear()
//        mutablePosts.add(position, Posts(binding.geteditpost.text.toString()))
//        getposts()
//        adapter.notifyDataSetChanged()
        database?.userDao()?.updatePost(Posts())?.
        subscribeOn(Schedulers.io())?.
        observeOn(AndroidSchedulers.mainThread())?.
        subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable?) {

            }
            override fun onComplete() {
                Log.e("updatePost", "onComplete: successfully")
            }

            override fun onError(e: Throwable?) {
            }
        })


    }
}