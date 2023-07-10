package com.example.submissionstoryapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityLoginBinding
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.view_model.LoginViewModel
import com.example.submissionstoryapp.ui.view_model.UserViewModelFactory

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "user")
class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        supportActionBar?.title = ""

        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, UserViewModelFactory(pref))[LoginViewModel::class.java]

        loginBinding.tvFailedLogin.visibility = View.GONE
        loginLiveData()
        loginBinding.btnLogin.isEnabled = false
        validateEditText()
        setupRegister()

        loginBinding.btnLogin.setOnClickListener { loginAccount() }
    }

    private fun loginAccount() {
        val loginEmail = loginBinding.edLoginEmail.text.toString()
        val loginPassword = loginBinding.edLoginPassword.text.toString()
        loginViewModel.loginAccount(loginEmail, loginPassword)
    }

    private fun loginLiveData() {
        loginViewModel.isLoading.observe(this){
            loginBinding.pbLogin.visibility =
                if(it) View.VISIBLE else View.GONE
        }

        loginViewModel.failedLogin.observe(this){
            loginBinding.tvFailedLogin.visibility =
                if(it) View.VISIBLE else View.GONE

        }

        loginViewModel.incorrectLogin.observe(this){
            loginBinding.tvFailedLogin.text =
                if(it) resources.getString(R.string.login_incorrect)
                else resources.getString(R.string.login_failed)
        }

        loginViewModel.successLogin.observe(this){
            if(it){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }

    private fun setupRegister() {
        val registerSuccess = intent.getBooleanExtra(REGISTER_SUCCESS, false)
        loginBinding.registerStatus.visibility =
            if(registerSuccess) View.VISIBLE else View.GONE
    }

    private fun validateEditText() {
        loginBinding.edLoginPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginBinding.registerStatus.visibility = View.GONE
                validateLoginButton()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        loginBinding.edLoginEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginBinding.registerStatus.visibility = View.GONE
                validateLoginButton()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    private fun validateLoginButton() {
        loginBinding.btnLogin.isEnabled = loginBinding.edLoginEmail.error==null
                && loginBinding.edLoginPassword.error==null
                && loginBinding.edLoginEmail.text !=null
                && loginBinding.edLoginEmail.text.toString().isNotEmpty()
                && loginBinding.edLoginPassword.text !=null
                && loginBinding.edLoginPassword.text.toString().isNotEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
        }
        return true
    }

    companion object{
        const val REGISTER_SUCCESS = "register_success"
    }
}