package com.alf.passwordmanagerv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alf.passwordmanagerv2.data.Security
import com.alf.passwordmanagerv2.databinding.ActivityFragmentContainerBinding

class FragmentContainer : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_password_manager -> prepareFragment(ManagerFragment())
                R.id.nav_security_dashboard -> prepareFragment(SecurityFragment())
                R.id.nav_user_settings -> prepareFragment(SettingsFragment())
                else -> false
            }
        }

        prepareFragment(ManagerFragment())
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
        Security.clear()
    }
}