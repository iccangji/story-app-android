package com.example.submissionstoryapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityOnboardPageBinding

class OnboardPageActivity: AppCompatActivity(){
    private lateinit var onboardPageBinding: ActivityOnboardPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardPageBinding = ActivityOnboardPageBinding.inflate(layoutInflater)
        setContentView(onboardPageBinding.root)

        supportActionBar?.hide()
        onboardPageBinding.btnLogin.setOnClickListener {
            val intent = Intent(this@OnboardPageActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        onboardPageBinding.btnRegister.setOnClickListener{
            val intent = Intent(this@OnboardPageActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}