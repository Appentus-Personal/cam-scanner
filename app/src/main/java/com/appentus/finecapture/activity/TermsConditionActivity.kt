package com.appentus.finecapture.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.appentus.finecapture.R

class TermsConditionActivity : AppCompatActivity() {

    lateinit var backIcn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)

        backIcn = findViewById(R.id.iv_back)
        backIcn.setOnClickListener {

            onBackPressed()
        }
    }

}