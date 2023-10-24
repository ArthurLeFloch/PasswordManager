package com.alf.passwordmanagerv2

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alf.passwordmanagerv2.databinding.ActivityNewAccountBinding
import com.alf.passwordmanagerv2.stats.searchPassword
import com.google.android.material.slider.RangeSlider
import java.io.File
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

    private fun fileName(): String {
        val service = binding.service.text.toString()
        val login = binding.login.text.toString()
        return service.trim() + "_" + login.trim()
    }

    private fun checkForm(): Boolean {
        if (binding.service.text.toString().trim() == "") {
            binding.service.error = "Entrez un nom de service"
            binding.service.requestFocus()
            return false
        }
        if (!binding.service.text.toString().matches(Regex("[a-zA-Z0-9 ]+"))) {
            val nonAcceptedChars = binding.service.text.toString().filter { !it.isLetterOrDigit() }
            val uniqueNonAcceptedChars = nonAcceptedChars.toSet().joinToString("")
            binding.service.error = "Caractères non acceptés : $uniqueNonAcceptedChars"
            binding.service.requestFocus()
            return false
        }
        if (binding.login.text.toString().trim() == "") {
            binding.login.error = "Entrez un nom d'utilisateur"
            binding.login.requestFocus()
            return false
        }
        if (!binding.login.text.toString().matches(Regex("[a-zA-Z0-9 ]+"))) {
            val nonAcceptedChars = binding.login.text.toString().filter { !it.isLetterOrDigit() }
            val uniqueNonAcceptedChars = nonAcceptedChars.toSet().joinToString("")
            binding.login.error = "Caractères non acceptés : $uniqueNonAcceptedChars"
            binding.login.requestFocus()
            return false
        }
        if (binding.password.text.toString().trim() == "") {
            binding.password.error = "Entrez un mot de passe"
            binding.password.requestFocus()
            return false
        }
        if (File(this.filesDir.absolutePath + "/storedData/" + fileName()).exists()) {
            binding.service.error = "Ce compte existe déjà"
            binding.service.requestFocus()
            return false
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