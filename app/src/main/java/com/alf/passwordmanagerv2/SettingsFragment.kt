package com.alf.passwordmanagerv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

private const val TAG = "SettingsFragmentTag"

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.settings_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference?>("feedback")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:alf.github@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(intent, "Send email"))
            true
        }

        findPreference<Preference?>("change_password")?.setOnPreferenceClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
            true
        }

        findPreference<Preference?>("small_account_layout")?.setOnPreferenceChangeListener { _, newValue ->
            Log.d(TAG, "Small account layout: $newValue")
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}