package com.alf.passwordmanagerv2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alf.passwordmanagerv2.data.Account
import com.alf.passwordmanagerv2.data.Security
import com.alf.passwordmanagerv2.databinding.FragmentSecurityBinding
import com.alf.passwordmanagerv2.utils.accountsToChange

class SecurityFragment : Fragment() {

    private lateinit var binding: FragmentSecurityBinding

    private lateinit var dataset: MutableList<Account>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.security_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecurityBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadDataset()

        binding.accounts.layoutManager = LinearLayoutManager(requireContext())
        binding.accounts.adapter = SecurityAccountAdapter(requireContext(), binding.root, dataset)

        updateVisibility()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        dataset =
            accountsToChange(Security.getAccounts(), days = daysBeforeReminder()).toMutableList()
        updateVisibility()
        binding.accounts.adapter?.notifyDataSetChanged()
    }

    private fun daysBeforeReminder(): Int {
        return PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getInt("days_before_password_reminder", 90)
    }

    private fun updateVisibility() {
        if (dataset.size == 0) {
            binding.nothingToDo.visibility = View.VISIBLE
        } else {
            binding.nothingToDo.visibility = View.GONE
        }
    }

    private fun reloadDataset() {
        dataset =
            accountsToChange(Security.getAccounts(), days = daysBeforeReminder()).toMutableList()
    }

}