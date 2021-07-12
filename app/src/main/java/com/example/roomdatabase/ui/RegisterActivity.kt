package com.example.roomdatabase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.roomdatabase.DatabaseClint
import com.example.roomdatabase.R
import com.example.roomdatabase.databinding.ActivityRegisterBinding
import com.example.roomdatabase.pojo.User
import com.example.roomdatabase.utils.Utils
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RegisterActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterBinding
    var utils: Utils?= Utils()

    var getEmailMutableLiveData = MutableLiveData<Boolean>()
    fun getEmailLiveData(): LiveData<Boolean?>? {
        return getEmailMutableLiveData
    }
    var getPasswordMutableLiveData = MutableLiveData<Boolean>()
    fun getPasswordLiveData(): LiveData<Boolean?>?{
        return getPasswordMutableLiveData
    }
    var getConfirmPasswordMutableLiveData = MutableLiveData<Boolean>()
    fun getConfirmPasswordLiveData(): LiveData<Boolean?>?{
        return getConfirmPasswordMutableLiveData
    }
    var password = MutableLiveData<CharSequence>()
    var check = MutableLiveData<Boolean>()

    var passwordObservable: Observable<Boolean>?=null
    var confirmPasswordObservable: Observable<Boolean>? = null
    var emailObservable: Observable<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this,R.layout.activity_register)

        setUpUi()
        setUPObserver()
    }

    private fun setUPObserver() {
        getEmailMutableLiveData.value = false
        getPasswordMutableLiveData.value = false
        getConfirmPasswordMutableLiveData.value =false

        getEmailLiveData()?.observe(this, androidx.lifecycle.Observer { boolean ->
            check.value = true
        })
        getPasswordLiveData()?.observe(this, androidx.lifecycle.Observer { boolean ->
            check.value = true
        })
        getConfirmPasswordMutableLiveData.observe(this, androidx.lifecycle.Observer { boolean ->
            check.value = true
        })
        check.observe(this, androidx.lifecycle.Observer { check ->
            if (getEmailLiveData()!!.value!! && getPasswordLiveData()!!.value!! && getConfirmPasswordLiveData()!!.value!!) {
                   binding!!.insert.isEnabled = true
            } else binding!!.insert!!.isEnabled = false
        })
    }

    private fun setUpUi() {
        validation()
        val database : DatabaseClint = DatabaseClint.getInstance(this)
        binding.insert.setOnClickListener(View.OnClickListener {
            database.userDao()
                ?.insert(User(binding.email.text.toString(), binding.password.text.toString()))!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onComplete() {
                        Toast.makeText(this@RegisterActivity, "register done", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable?) {
                        Log.e("register", "onError: "+e?.message )
                    }
                })
        })

        binding.loginPage.setOnClickListener(View.OnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

    }

    fun validation(){
        utils!!.validText(binding!!.email,object:Utils.ValidEditText{
            override fun valid() {
                binding!!.emailInputLayout.error = "Invalid email"
                emailObservable = binding!!.email.textChanges()
                        .map { inputText: CharSequence ->
                            inputText.toString()
                                    .matches("(?:[a-z0-9!#$%'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex())
                        }
                        .distinctUntilChanged()
                emailObservable?.subscribe { isValid: Boolean? ->
                    binding!!.emailInputLayout.isErrorEnabled = !isValid!!
                }
                emailObservable?.subscribe { isValid: Boolean? ->
                    getEmailMutableLiveData.postValue(isValid)
                }
            }
        })
        utils!!.validText(binding!!.password,object :Utils.ValidEditText{
            override fun valid() {
                password.postValue(binding?.password?.text)
                binding!!.passwordInputLayout.error = "Invalid password"
                passwordObservable = binding!!.password.textChanges()
                        .map { inputText: CharSequence ->
                            inputText.toString().matches("^(?=.*\\d).{4,8}$".toRegex())
                        }
                        .distinctUntilChanged()
                passwordObservable?.subscribe { isVaild: Boolean? ->
                    binding!!.passwordInputLayout.isErrorEnabled = !isVaild!!
                }
                passwordObservable?.subscribe { isValid: Boolean? ->
                    getPasswordMutableLiveData.postValue(isValid)
                }
            }
        })

        utils!!.validText(binding!!.confirmPassword,object:Utils.ValidEditText{
            override fun valid() {
                binding!!.confirmPasswordInputLayout.error = "Invalid password"
                confirmPasswordObservable = binding!!.confirmPassword.textChanges()
                        .map { inputText: CharSequence ->
                            inputText.toString().equals(password.value.toString())
                        }.distinctUntilChanged()
                confirmPasswordObservable?.subscribe { isVaild: Boolean? ->
                    binding!!.confirmPasswordInputLayout.isErrorEnabled = !isVaild!!
                }
                confirmPasswordObservable?.subscribe { isValid: Boolean? ->
                    getConfirmPasswordMutableLiveData.postValue(isValid)
                }
            }
        })
    }
}