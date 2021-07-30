package com.templatemela.camscanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.templatemela.camscanner.R

class TermsConditionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)
    }

    fun onClick(view: View) {
        onBackPressed()
    }
}