package com.alf.passwordmanagerv2

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.alf.passwordmanagerv2.databinding.ActivityNewAccountBinding
import com.alf.passwordmanagerv2.utils.searchPassword
import com.google.android.material.slider.RangeSlider
import kotlin.random.Random

private const val TAG = "NewAccountTag"

class NewAccount : AppCompatActivity() {

    private lateinit var binding: ActivityNewAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Nouveau compte"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding = ActivityNewAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generatePassword()

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
        binding.passwordSlider.addOnChangeListener(RangeSlider.OnChangeListener { _, _, _ -> generatePassword() })

        binding.generatePassword.setOnClickListener {
            generatePassword()
        }
        binding.save.setOnClickListener {
            onValidation()
        }

        binding.passwordSlider.values = listOf(
            PreferenceManager.getDefaultSharedPreferences(this).getInt("default_password_size", 8)
                .toFloat()
        )
    }

    private fun generatePassword() {
        val size = binding.passwordSlider.values[0].toInt()
        var allowedChars = ""
        var password = ""
        for (i in 33 until 127) {
            allowedChars += i.toChar()
        }
        Log.d(TAG, allowedChars)
        for (i in 0 until size) {
            password += allowedChars[Random.nextInt(0, allowedChars.length - 1)]
        }
        Log.d(TAG, password)
        binding.password.setText(password)
    }

    private fun checkForm(): Boolean {
        val service = binding.service.text.toString().trim()
        val login = binding.login.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (service == "") {
            binding.service.error = "Entrez un nom de service"
            binding.service.requestFocus()
            return false
        }
        if (login == "") {
            binding.login.error = "Entrez un nom d'utilisateur"
            binding.login.requestFocus()
            return false
        }
        if (password == "") {
            binding.password.error = "Entrez un mot de passe"
            binding.password.requestFocus()
            return false
        }

        for (account in User.dataset) {
            if (account.service == service && account.login == login) {
                binding.service.error = "Ce compte existe déjà"
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
        User.createAccount(service, login, password)
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
            }
        }

        if (checkForm()) {
            searchPassword(this, binding.password.text.toString(), ::onResult)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}