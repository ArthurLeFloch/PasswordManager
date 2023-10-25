package com.alf.passwordmanagerv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.alf.passwordmanagerv2.databinding.ActivityEditAccountPasswordBinding
import com.alf.passwordmanagerv2.utils.searchPassword
import com.google.android.material.slider.RangeSlider
import kotlin.random.Random

class EditAccountPassword : AppCompatActivity() {

    private lateinit var binding: ActivityEditAccountPasswordBinding
    private var id: Int = -1
    private lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = "Modification du compte"

        id = intent.getIntExtra("id", -1)
        account = User.getAccount(id)

        binding.service.text = account.service
        binding.login.text = account.login

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.passwordInput.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.password.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.password.clearFocus()
                onValidation()
                true
            } else false
        }
        binding.newPasswordSlider.addOnChangeListener(RangeSlider.OnChangeListener { _, _, _ -> generatePassword() })

        binding.generateNewPassword.setOnClickListener {
            generatePassword()
        }
        binding.newConfirm.setOnClickListener {
            onValidation()
        }

        binding.newPasswordSlider.values = listOf(
            PreferenceManager.getDefaultSharedPreferences(this).getInt("default_password_size", 8)
                .toFloat()
        )
    }

    private fun generatePassword() {
        val size = binding.newPasswordSlider.values[0].toInt()
        var allowedChars = ""
        var password = ""
        for (i in 33 until 127) {
            allowedChars += i.toChar()
        }
        for (i in 0 until size) {
            password += allowedChars[Random.nextInt(0, allowedChars.length - 1)]
        }
        binding.password.setText(password)
    }

    private fun onValidation() {
        val password = binding.password.text.toString().trim()
        fun onResult(result: Boolean) {
            if (result) {
                User.changeAccountPassword(id, password)
                finish()
            } else {
                binding.password.requestFocus()
            }
        }

        when (password) {
            account.getPassword() -> {
                binding.passwordInput.error = "Mot de passe déjà actif !"
            }

            "" -> {
                binding.passwordInput.error = "Veuillez renseigner un mot de passe."
            }

            else -> {
                searchPassword(this, password, ::onResult)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}