package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Thread {
            Thread.sleep(3000) // 3 seconds delay
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }.start()
    }
}

