package com.alf.passwordmanagerv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alf.passwordmanagerv2.databinding.ActivityFragmentContainerBinding
import java.util.concurrent.Executors

class FragmentContainer : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val executor = Executors.newSingleThreadExecutor()

        var isLoaded = false

        binding.progressBar.progress = 0
        binding.progressBar.visibility = View.VISIBLE

        fun onInit(amount: Int) {
            binding.progressBar.max = amount
        }

        fun onUpdate(current: Int) {
            binding.progressBar.progress = current
        }

        fun onFinish() {
            binding.progressBar.visibility = View.GONE
            prepareFragment(ManagerFragment())
            isLoaded = true
        }

        executor.execute {
            User.loadAccounts(::onInit, ::onUpdate, ::onFinish)
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            if (!isLoaded) return@setOnItemSelectedListener false
            when (it.itemId) {
                R.id.nav_password_manager -> prepareFragment(ManagerFragment())
                R.id.nav_security_dashboard -> prepareFragment(SecurityFragment())
                R.id.nav_user_settings -> prepareFragment(SettingsFragment())
                else -> false
            }
        }
    }

    private fun prepareFragment(fragment: Fragment): Boolean {
        binding.progressBar.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(
            binding.navHostFragment.id, fragment
        ).commit()
        binding.progressBar.visibility = View.GONE
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        User.clear()
    }
}