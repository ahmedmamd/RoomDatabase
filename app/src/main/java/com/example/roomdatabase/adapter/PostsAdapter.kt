package com.example.roomdatabase.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.DatabaseClint
import com.example.roomdatabase.R
import com.example.roomdatabase.pojo.Posts
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PostsAdapter(
      private val posts:MutableList<Posts?>,
      val context: Context,
      val itemChoosen: Itemclicked
    ): RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    var deletePostMutableLiveData = MutableLiveData<Boolean>()
    fun deletePostsLiveData(): LiveData<Boolean>?{
        return deletePostMutableLiveData
    }

    class PostsViewHolder (view:View):RecyclerView.ViewHolder(view){
        var body : TextView
        var delete: Button
        var update: Button?
        init {
            delete =itemView.findViewById(R.id.delete)
            update =itemView.findViewById(R.id.update)
            body=itemView.findViewById(R.id.id_editPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
         return PostsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
         holder.body.text = posts[position]?.posts.toString()
         val database: DatabaseClint = DatabaseClint.getPosts(context)
         holder.update?.setOnClickListener(View.OnClickListener {

//             itemChoosen.itemChoose(position)

             database.userDao()?.updatePost(posts.set(position,Posts("first post")))?.
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

         })
         holder.delete.setOnClickListener(View.OnClickListener {

             database.userDao()?.deletePost(posts[position])
                     ?.subscribeOn(Schedulers.io())
                     ?.observeOn(AndroidSchedulers.mainThread())
                     ?.subscribe(object : CompletableObserver {
                         override fun onSubscribe(d: Disposable?) {
                         }

                         override fun onComplete() {
                             deletePostMutableLiveData.postValue(true)
                             Log.e("delete", "onComplete: deleted ")
                         }

                         override fun onError(e: Throwable?) {
                             Log.e("delete", "onError: "+e?.message )
                         }
                     })
         })
         holder.itemView.setOnClickListener(View.OnClickListener {

            Toast.makeText(context,"item choose",Toast.LENGTH_SHORT).show()
        })
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    open interface Itemclicked{
        fun itemChoose(position: Int)
    }
}