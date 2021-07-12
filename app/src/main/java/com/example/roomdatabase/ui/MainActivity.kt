package com.example.roomdatabase.ui

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.roomdatabase.DatabaseClint
import com.example.roomdatabase.R
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.pojo.User
import com.example.roomdatabase.utils.Utils
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


 class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
     var emailObservable: Observable<Boolean>? = null
     var passwordObservable:Observable<Boolean>?=null
         var utils:Utils?=Utils()

     var getEmailMutableLiveData =MutableLiveData<Boolean>()
     fun getEmailLiveData():LiveData<Boolean>?{
         return getEmailMutableLiveData
     }
     var getPasswordMutableLiveData =MutableLiveData<Boolean>()
     fun getPasswordLiveData():LiveData<Boolean>?{
         return getPasswordMutableLiveData
     }
     var check = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpObserver()
        setUpUi()
    }
     private fun setUpObserver() {
          getEmailMutableLiveData.value=false
          getPasswordMutableLiveData.value=false

          getEmailLiveData()?.observe(this, Observer { boolean->
                  check.value=true
          })
          getPasswordLiveData()?.observe(this, Observer { boolean->
                 check.value=true
          })
          check.observe(this, Observer { boolean->
             binding.login.isEnabled = getEmailLiveData()?.value!! && getPasswordLiveData()?.value!!
           })
     }

     private fun setUpUi() {
        validation()
        val database : DatabaseClint = DatabaseClint.getInstance(this)
         binding.login.setOnClickListener(View.OnClickListener {
             database.userDao()?.getAll()?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : SingleObserver<List<User?>?> {
                 override fun onSubscribe(d: Disposable?) {
                 }
                 override fun onSuccess(users: List<User?>?) {
                     Log.e("onSuccess", "onSuccess: "+users?.get(0) )
                     for (i in users!!.indices) {
                         if (users?.get(i)!!.email!!.equals(binding.email.text.toString()) && users?.get(i)!!.password.equals(binding.password.text.toString())) {
                             val intent = Intent(this@MainActivity, DataActivity::class.java)
                             startActivity(intent)
                         }
                     }
                 }
                 override fun onError(e: Throwable?) {
                 }
             })
         })
         binding.register.setOnClickListener(View.OnClickListener {
             val intent=Intent(this@MainActivity, RegisterActivity::class.java)
             startActivity(intent)
         })
    }
     fun validation(){
         utils!!.validText(binding!!.email ,object : Utils.ValidEditText {
             override fun valid() {
                 binding!!.emailInputLayout.error = "Invalid email"
                 emailObservable = binding!!.email.textChanges()
                         .map { inputText: CharSequence ->
                              inputText.toString()
                             .matches("(?:[a-z0-9!#$%'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex())
                         }.distinctUntilChanged()
                     emailObservable?.subscribe { isValid: Boolean? ->
                     binding!!.emailInputLayout.isErrorEnabled = !isValid!!
                     getEmailMutableLiveData.postValue(isValid)
                 }
             }
         })

            utils!!.validText(binding!!.password, object : Utils.ValidEditText {
                override fun valid() {
                    binding!!.passwordInputLayout.error = "Invalid passwod"
                    passwordObservable =binding!!.password.textChanges()
                            .map { inputText:CharSequence->
                                   inputText!!.toString().equals("")
                                 }.distinctUntilChanged()
                    passwordObservable?.subscribe{isVaild:Boolean?->
                            binding!!.passwordInputLayout.isErrorEnabled=isVaild!!
                            getPasswordMutableLiveData.postValue(!isVaild!!)
                    }
                }
            })
     }
    }







