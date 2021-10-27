package com.appentus.camscanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.appentus.camscanner.R

class PrivacyPolicy : AppCompatActivity() {

    lateinit var backIcn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        backIcn = findViewById(R.id.iv_back)
        backIcn.setOnClickListener {

            onBackPressed()
        }
    }


}