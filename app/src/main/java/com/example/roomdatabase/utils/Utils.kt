package com.example.roomdatabase.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class Utils {
    open interface ValidEditText{
        fun valid()
    }

    open fun validText(editText: EditText , validEditText: ValidEditText){
        editText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               validEditText.valid()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}