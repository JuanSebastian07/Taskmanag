package com.projects.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.projects.projemanag.databinding.ActivitySplashBinding
import com.projects.projemanag.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {

    private var binding : ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Cambiamos la fuente de la letra
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding?.tvAppName?.typeface = typeface

        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                // Start the Intro Activity
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                // Start the Intro Activity
                startActivity(Intent(this, IntroActivity::class.java))
            }
            // Call this when your activity is done and should be closed.
            finish ()
        }, 2500)
    }
}