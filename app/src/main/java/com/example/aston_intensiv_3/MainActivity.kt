package com.example.aston_intensiv_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonFlags = findViewById<Button>(R.id.button_flags)
        val buttonPicturePicasso = findViewById<Button>(R.id.button_load_picture_picasso)
        val buttonPictureDefault = findViewById<Button>(R.id.button_load_picture_default)

        buttonFlags.setOnClickListener { startActivity(Intent(this, FlagsActivity::class.java)) }
        buttonPicturePicasso.setOnClickListener { startActivity(Intent(this, LoadPicturePicassoActivity::class.java)) }
        buttonPictureDefault.setOnClickListener { startActivity(Intent(this, LoadPictureDefaultActivity::class.java)) }
    }
}