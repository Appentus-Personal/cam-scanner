package com.app.camscan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.camscan.R

class WelcomeActivity : AppCompatActivity() {

    //lateinit var binding: ActivityWelcomeBinding

    var tvStarted: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //binding = ActivityWelcomeBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        tvStarted = findViewById<TextView>(R.id.tvStarted)

        tvStarted!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}