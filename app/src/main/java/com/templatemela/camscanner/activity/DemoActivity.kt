package com.templatemela.camscanner.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.templatemela.camscanner.R
import com.templatemela.camscanner.adapter.AllGroupAdapter
import com.templatemela.camscanner.models.DBModel
import java.util.*

class DemoActivity : AppCompatActivity() {

    lateinit var editText:EditText
    var groupList = ArrayList<DBModel>()

     lateinit var allGroupAdapter: AllGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


        editText = findViewById(R.id.et_search)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                if (i3 == 0) {
//                    iv_more.setVisibility(View.INVISIBLE)
                } else if (i3 == 1) {
//                    iv_clear_txt.setVisibility(View.VISIBLE)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (groupList.size > 0) {
                    filter(editable.toString())
                }
            }
        })
    }


    fun filter(str: String) {
        val arrayList: ArrayList<DBModel> = ArrayList<DBModel>()
        val it: Iterator<DBModel> = groupList.iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (next.getGroup_name().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(next)
            }
        }
        allGroupAdapter.filterList(arrayList)
    }


}


