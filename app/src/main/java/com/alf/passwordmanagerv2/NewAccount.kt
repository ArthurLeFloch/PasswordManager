package com.alf.passwordmanagerv2

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.alf.passwordmanagerv2.data.Security
import com.alf.passwordmanagerv2.databinding.ActivityNewAccountBinding
import com.alf.passwordmanagerv2.utils.generatePassword
import com.alf.passwordmanagerv2.utils.searchPassword
import com.google.android.material.slider.RangeSlider

private const val TAG = "NewAccountTag"

class NewAccount : AppCompatActivity() {

    private lateinit var binding: ActivityNewAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.new_account_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding = ActivityNewAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.password.setText(generatePassword(passwordSize()))

        binding.service.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.login.requestFocus()
                true
            } else {
                false
            }
        }
        binding.login.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.password.requestFocus()
                true
            } else {
                false
            }
        }
        binding.password.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                onValidation()
                true
            } else false
        }
        binding.passwordSlider.addOnChangeListener(RangeSlider.OnChangeListener { _, _, _ ->
            binding.password.setText(generatePassword(passwordSize()))
        })

        binding.generatePassword.setOnClickListener {
            binding.password.setText(generatePassword(passwordSize()))
        }
        binding.save.setOnClickListener {
            onValidation()
        }

        binding.passwordSlider.values = listOf(
            PreferenceManager.getDefaultSharedPreferences(this).getInt("default_password_size", 8)
                .toFloat()
        )
    }

    private fun passwordSize(): Int {
        return binding.passwordSlider.values[0].toInt()
    }

    private fun checkForm(): Boolean {
        val service = binding.service.text.toString().trim()
        val login = binding.login.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (service == "") {
            binding.service.error = getString(R.string.account_service_empty)
            binding.service.requestFocus()
            return false
        }
        if (login == "") {
            binding.login.error = getString(R.string.account_login_empty)
            binding.login.requestFocus()
            return false
        }
        if (password == "") {
            binding.password.error = getString(R.string.account_password_empty)
            binding.password.requestFocus()
            return false
        }

        for (account in Security.getAccounts()) {
            if (account.service == service && account.login == login) {
                binding.service.error = getString(R.string.account_already_exists)
                binding.service.requestFocus()
                return false
            }
        }

        return true
    }

    private fun createAccount() {
        val service = binding.service.text.toString()
        val login = binding.login.text.toString()
        val password = binding.password.text.toString()
        Security.createAccount(service, login, password)
        finish()
    }

    private fun onValidation() {
        binding.service.error = null
        binding.login.error = null
        binding.password.error = null

        fun onResult(result: Boolean) {
            if (result) {
                createAccount()
            } else {
                binding.password.requestFocus()
                binding.save.isEnabled = true
            }
        }

        if (checkForm()) {
            binding.save.isEnabled = false
            searchPassword(this, binding.password.text.toString(), ::onResult)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}