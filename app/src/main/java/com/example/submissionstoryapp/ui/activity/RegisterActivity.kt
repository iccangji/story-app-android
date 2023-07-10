package com.example.submissionstoryapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityRegisterBinding
import com.example.submissionstoryapp.ui.view_model.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        supportActionBar?.title = ""
        registerLiveData()
        registerBinding.btnRegister.isEnabled = false
        validateEditText()
        registerBinding.btnRegister.setOnClickListener{registerAccount()}
    }

    private fun registerLiveData() {
        registerViewModel.isLoading.observe(this){
            registerBinding.pbRegister.visibility =
                if(it) View.VISIBLE else View.GONE
        }
        registerViewModel.successRegister.observe(this){
            if(it){
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(LoginActivity.REGISTER_SUCCESS, true)
                startActivity(intent)
            }
        }
        registerViewModel.failedRegister.observe(this){
            registerBinding.tvFailedRegister.visibility = if(it)View.VISIBLE
            else View.GONE
        }

        registerViewModel.emailTaken.observe(this){
            registerBinding.tvFailedRegister.text =
                if(it) resources.getString(R.string.email_taken)
                else resources.getString(R.string.sign_up_failed)
        }
    }

    private fun registerAccount() {
        val signupName = registerBinding.edRegisterName.text.toString()
        val signupEmail = registerBinding.edRegisterEmail.text.toString()
        val signupPassword = registerBinding.edRegisterPassword.text.toString()
        registerViewModel.registerAccount(signupName, signupEmail, signupPassword)
    }

    private fun validateEditText() {
        registerBinding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registerViewModel.setFailedRegister(false)
                validateRegisterButton()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        registerBinding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registerViewModel.setFailedRegister(false)
                validateRegisterButton()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        registerBinding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registerViewModel.setFailedRegister(false)
                validateRegisterButton()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun validateRegisterButton() {
        registerBinding.btnRegister.isEnabled = registerBinding.edRegisterName.error==null
                && registerBinding.edRegisterEmail.error==null
                && registerBinding.edRegisterPassword.error == null
                && registerBinding.edRegisterName.text !=null
                && registerBinding.edRegisterName.text.toString().isNotEmpty()
                && registerBinding.edRegisterEmail.text !=null
                && registerBinding.edRegisterEmail.text.toString().isNotEmpty()
                && registerBinding.edRegisterPassword.text !=null
                && registerBinding.edRegisterPassword.text.toString().isNotEmpty()
    }
}