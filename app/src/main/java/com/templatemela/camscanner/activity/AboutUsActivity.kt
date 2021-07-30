package com.templatemela.camscanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.templatemela.camscanner.R

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }

    fun onClick(view: View) {
        onBackPressed()
    }
}