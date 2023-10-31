package com.alf.passwordmanagerv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.alf.passwordmanagerv2.data.Account
import com.alf.passwordmanagerv2.data.Security
import com.alf.passwordmanagerv2.databinding.ActivityEditAccountPasswordBinding
import com.alf.passwordmanagerv2.utils.generatePassword
import com.alf.passwordmanagerv2.utils.searchPassword
import com.google.android.material.slider.RangeSlider

class EditAccountPassword : AppCompatActivity() {

    private lateinit var binding: ActivityEditAccountPasswordBinding
    private var path: String? = null
    private lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = getString(R.string.edit_account_title)

        path = intent.getStringExtra("path")
        account = Security.getAccount(path!!)

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
        binding.newPasswordSlider.addOnChangeListener(RangeSlider.OnChangeListener { _, _, _ ->
            binding.password.setText(generatePassword(passwordSize()))
        })

        binding.generateNewPassword.setOnClickListener {
            binding.password.setText(generatePassword(passwordSize()))
        }
        binding.newConfirm.setOnClickListener {
            onValidation()
        }

        binding.newPasswordSlider.values = listOf(
            PreferenceManager.getDefaultSharedPreferences(this).getInt("default_password_size", 8)
                .toFloat()
        )
    }

    private fun passwordSize(): Int {
        return binding.newPasswordSlider.values[0].toInt()
    }

    private fun onValidation() {
        val password = binding.password.text.toString().trim()

        fun onResult(result: Boolean) {
            if (result) {
                Security.changeAccountPassword(account, password)
                finish()
            } else {
                binding.password.requestFocus()
                binding.newConfirm.isEnabled = true
            }
        }

        when (password) {
            account.getPassword() -> {
                binding.passwordInput.error = getString(R.string.password_already_used)
            }

            "" -> {
                binding.passwordInput.error = getString(R.string.ask_write_password)
            }

            else -> {
                binding.newConfirm.isEnabled = false
                searchPassword(this, password, ::onResult)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}