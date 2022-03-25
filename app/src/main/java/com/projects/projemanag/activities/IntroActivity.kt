package com.projects.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.projects.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private var binding : ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding?.tvAppNameIntro?.typeface = typeface


        binding?.btnSignInIntro?.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }
        binding?.btnSignUpIntro?.setOnClickListener {
            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}