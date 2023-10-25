package com.alf.passwordmanagerv2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alf.passwordmanagerv2.databinding.ActivityAuthWallBinding
import com.alf.passwordmanagerv2.security.MasterPassword

private const val TAG = "AuthWallTag"

class AuthWall : AppCompatActivity() {

    private lateinit var binding: ActivityAuthWallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthWallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Gestionnaire de mots de passe"

        User.init(this.filesDir.absolutePath)

        if (User.isFirstLog()) {
            val intent = Intent(this, FirstLog::class.java)
            Log.d(TAG, "Switching to FirstLog activity")
            startActivity(intent)
        }

        binding.password.setOnClickListener { binding.passwordInput.error = null }
        binding.passwordInput.setOnClickListener { binding.passwordInput.error = null }

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
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val triedPassword = binding.password.text.toString()
                Log.d(TAG, "Trying a new password")
                if (MasterPassword.isEqual(triedPassword)) {
                    Log.d(TAG, "Access granted")
                    binding.password.setText("")
                    MasterPassword.set(triedPassword)
                    val intent = Intent(this, FragmentContainer::class.java)
                    startActivity(intent)
                } else {
                    binding.passwordInput.error = "Mot de passe saisi incorrect."
                }
                true
            } else false
        }

    }
}