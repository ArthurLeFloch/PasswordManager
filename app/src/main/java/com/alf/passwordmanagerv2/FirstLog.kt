package com.alf.passwordmanagerv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alf.passwordmanagerv2.databinding.ActivityFirstLogBinding
import com.alf.passwordmanagerv2.security.MasterPassword
import com.alf.passwordmanagerv2.utils.searchPassword

class FirstLog : AppCompatActivity() {
    private lateinit var binding: ActivityFirstLogBinding
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Gestionnaire de mots de passe"

        path = this.filesDir.absolutePath + "/storedData"
        binding.password.setOnClickListener { binding.passwordConfirmedInput.error = null }
        binding.passwordConfirmed.setOnClickListener { binding.passwordConfirmedInput.error = null }

        binding.password.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.password.clearFocus()
                binding.passwordConfirmed.requestFocus()
                true
            } else false
        }
        binding.passwordConfirmed.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                saveMasterKey()
                true
            } else false
        }
        binding.passwordConfirmed.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.passwordConfirmedInput.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onBackPressed() {
        //prevent from going back without setting up a password (necessary)
    }

    private fun saveMasterKey() {
        val password1 = binding.password.text.toString()
        val password2 = binding.passwordConfirmed.text.toString()

        fun onResult(result: Boolean) {
            if (result) {
                MasterPassword.set(password1)
                finish()
            } else {
                binding.password.setText("")
                binding.passwordConfirmed.setText("")
                binding.password.requestFocus()
            }
        }

        if (password1 == password2) {
            searchPassword(this, password1, ::onResult)
        } else {
            binding.passwordConfirmedInput.error = "Les mots de passe sont différents !"
        }
    }
}