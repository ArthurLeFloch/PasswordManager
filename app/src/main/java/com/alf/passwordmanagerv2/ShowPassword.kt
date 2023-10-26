package com.alf.passwordmanagerv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.alf.passwordmanagerv2.databinding.ActivityShowPasswordBinding

class ShowPassword : AppCompatActivity() {

    private lateinit var binding: ActivityShowPasswordBinding
    private lateinit var account: Account
    private var charPerScreen: Int = 0

    private var passwordLength: Int = 0
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding = ActivityShowPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)
        account = User.getAccount(id)

        binding.service.text = account.service
        binding.login.text = account.login
        binding.date.text = account.getLastEdit()

        passwordLength = account.getPassword().length

        charPerScreen =
            PreferenceManager.getDefaultSharedPreferences(this).getInt("char_per_screen", 8)
        currentIndex = 0

        binding.nextPart.setOnClickListener {
            if (currentIndex < viewAmount() - 1) {
                showNextPart()
            } else {
                finish()
            }
        }

        binding.previousPart.setOnClickListener {
            if (currentIndex > 0) {
                showPreviousPart()
            }
        }

        updateAll()
    }

    private fun viewAmount(): Int {
        val pages = passwordLength / charPerScreen
        if (passwordLength % charPerScreen == 0) {
            return pages
        }
        return pages + 1
    }

    private fun showPasswordPart() {
        val start = currentIndex * charPerScreen
        val end =
            if (currentIndex == viewAmount() - 1) passwordLength else (currentIndex + 1) * charPerScreen
        binding.passwordPart.text = account.getPassword().substring(start, end)
    }

    private fun updatePartText() {
        binding.partInfo.text = "${currentIndex + 1} / ${viewAmount()}"
    }

    private fun updateButtons() {
        if (currentIndex == viewAmount() - 1) {
            binding.nextPart.setIconResource(R.drawable.round_done_24)
            binding.nextPart.text = getString(R.string.close)
        } else {
            binding.nextPart.setIconResource(R.drawable.round_arrow_forward_24)
            binding.nextPart.text = getString(R.string.next)
        }

        if (currentIndex == 0) {
            binding.previousPart.visibility = android.view.View.INVISIBLE
        } else {
            binding.previousPart.visibility = android.view.View.VISIBLE
        }
    }

    private fun showNextPart() {
        if (currentIndex < viewAmount() - 1) {
            currentIndex++
        }
        updateAll()
    }

    private fun showPreviousPart() {
        if (currentIndex > 0) {
            currentIndex--
        }
        updateAll()
    }

    private fun updateAll() {
        showPasswordPart()
        updatePartText()
        updateButtons()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}