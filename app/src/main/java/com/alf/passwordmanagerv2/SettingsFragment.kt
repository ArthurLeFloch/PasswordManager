package com.alf.passwordmanagerv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = "Paramètres"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<Preference?>("notifications")?.setOnPreferenceChangeListener { _, newValue ->
            Log.d("Preferences", "Notifications enabled: $newValue")
            true
        }

        findPreference<Preference?>("feedback")?.setOnPreferenceClickListener {
            Log.d("Preferences", "Feedback was clicked")
            true
        }

        findPreference<Preference?>("volume")?.setOnPreferenceChangeListener { _, newValue ->
            Log.d("Preferences", "Volume: $newValue")
            true
        }

        findPreference<Preference?>("feedback")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:alf.github@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "My email subject")
            intent.putExtra(Intent.EXTRA_TEXT, "My email body")
            startActivity(Intent.createChooser(intent, "Send email"))
            true
        }

        findPreference<Preference?>("change_password")?.setOnPreferenceClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
            true
        }

        findPreference<Preference?>("small_account_layout")?.setOnPreferenceChangeListener { _, newValue ->
            Log.d("Preferences", "Small account layout: $newValue")
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


}