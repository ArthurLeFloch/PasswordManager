package com.alf.passwordmanagerv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alf.passwordmanagerv2.databinding.ActivityChangePasswordBinding
import com.alf.passwordmanagerv2.security.MasterPassword
import com.alf.passwordmanagerv2.utils.searchPassword


class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = "Changement de mot de passe"

        binding.password.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.password.clearFocus()
                binding.newPassword.requestFocus()
                true
            } else false
        }
        binding.newPassword.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.newPassword.clearFocus()
                binding.newPasswordConfirmed.requestFocus()
                true
            } else false
        }
        binding.newPasswordConfirmed.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                onValidation()
                true
            } else false
        }

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.passwordInput.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.newPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.newPasswordInput.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.newPasswordConfirmed.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.newPasswordConfirmedInput.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun onValidation() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        val triedPassword = binding.password.text.toString().trim()
        val newPassword = binding.newPassword.text.toString().trim()
        val newPasswordConfirmed = binding.newPasswordConfirmed.text.toString().trim()

        fun onResult(result: Boolean) {
            if (result) {
                // TODO: use progress bar here, new onResult function
                User.changeMasterPassword(newPassword)
                finish()
            } else {
                binding.password.requestFocus()
                binding.progressBar.visibility = android.view.View.GONE
            }
        }

        if (MasterPassword.isEqual(triedPassword)) {
            if (newPassword == newPasswordConfirmed) {
                if (newPassword != "") {
                    searchPassword(this, newPassword, ::onResult)
                } else {
                    binding.newPasswordConfirmedInput.error =
                        "Veuillez indiquer le nouveau mot de passe."
                    binding.progressBar.visibility = android.view.View.GONE
                }
            } else {
                binding.newPasswordConfirmedInput.error = "Les mots de passe sont différents !"
                binding.progressBar.visibility = android.view.View.GONE
            }
        } else {
            binding.passwordInput.error = "Mot de passe saisi incorrect."
            binding.progressBar.visibility = android.view.View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}