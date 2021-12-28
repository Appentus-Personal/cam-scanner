package com.appentus.finecapture.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.appentus.finecapture.R

class AboutUsActivity : AppCompatActivity() {

    lateinit var backIcn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        backIcn = findViewById(R.id.iv_back)
        backIcn.setOnClickListener {

            onBackPressed()
        }

    }


}