package com.alf.passwordmanagerv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alf.passwordmanagerv2.databinding.FragmentManagerBinding

private const val TAG = "ManagerFragmentTag"

class ManagerFragment : Fragment() {

    private lateinit var binding: FragmentManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.management_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = AccountAdapter(requireContext(), binding.root)

        binding.add.setOnClickListener { addData() }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.manager_menu, menu)

                val searchMenuItem = menu.findItem(R.id.action_search)
                val searchView = searchMenuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_hint)

                searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        Log.d(TAG, "Search expanded")
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        Log.d(TAG, "Search collapsed")
                        (binding.recyclerView.adapter as AccountAdapter).reloadDataset()
                        return true
                    }
                })

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d(TAG, "Search submitted")
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        (binding.recyclerView.adapter as AccountAdapter).addFilter(newText!!.trim())
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return menuItem.itemId == R.id.action_search
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    override fun onResume() {
        super.onResume()
        (binding.recyclerView.adapter as AccountAdapter).reloadDataset()
    }

    private fun addData() {
        val intent = Intent(requireContext(), NewAccount::class.java)
        startActivity(intent)
        Log.d(TAG, "Switching to NewAccount activity")
    }
}