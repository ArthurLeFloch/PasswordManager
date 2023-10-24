package com.alf.passwordmanagerv2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alf.passwordmanagerv2.databinding.ActivityMainBinding
import com.alf.passwordmanagerv2.security.MasterPassword

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Gestionnaire de mots de passe"

        User.init(this.filesDir.absolutePath)

        if (User.isFirstLog()) {
            val intent = Intent(this, FirstLogActivity::class.java)
            Log.d(TAG, "First log")
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
                Log.d(TAG, "tried password: $triedPassword")
                if (MasterPassword.isEqual(triedPassword)) {
                    Log.d(TAG, "Accès autorisé")
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