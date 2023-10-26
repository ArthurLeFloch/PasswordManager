package com.alf.passwordmanagerv2

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AccountAdapter(
    private val context: Context, private val root: View
) : RecyclerView.Adapter<AccountAdapter.ItemViewHolder>() {

    private var layout: Int = -1

    init {
        val isSmallLayout = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("small_account_layout", false)
        layout = if (isSmallLayout) R.layout.small_account_layout else R.layout.account_layout
    }

    private var dataset: MutableList<Account> = User.dataset
    private var currentDataset: MutableList<Account> = User.dataset
    private var isFiltered: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun addFilter(text: String) {
        val filteredDataset = mutableListOf<Account>()
        for (account in dataset) {
            if (account.service.contains(text, true) || account.login.contains(text, true)) {
                filteredDataset.add(account)
            }
        }
        currentDataset = filteredDataset
        notifyDataSetChanged()
        isFiltered = true
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeFilter() {
        currentDataset = dataset
        notifyDataSetChanged()
        isFiltered = false
    }

    private fun getAccount(position: Int): Account {
        return currentDataset[position]
    }

    private fun getRealAccountPosition(position: Int): Int {
        return dataset.indexOf(currentDataset[position])
    }

    private fun removeAccount(position: Int) {
        currentDataset.removeAt(position)
    }

    private fun updateFrom(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    private fun onEdit(position: Int): Boolean {
        val intent = Intent(context, EditAccountPassword::class.java)
        intent.putExtra("id", getRealAccountPosition(position))
        context.startActivity(intent)
        return true
    }

    private fun onCopy(position: Int) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", getAccount(position).getPassword()))
        Snackbar.make(
            root,
            context.getString(R.string.snackbar_password_copied),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onView(position: Int) {
        val intent = Intent(context, ShowPassword::class.java)
        intent.putExtra("id", getRealAccountPosition(position))
        context.startActivity(intent)
    }

    private fun onDelete(position: Int) {
        MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.dialog_delete_account))
            .setMessage(context.getString(R.string.dialog_delete_text_info))
            .setPositiveButton(context.getString(R.string.dialog_delete)) { _: DialogInterface, _: Int ->
                Snackbar.make(
                    root,
                    context.getString(R.string.snackbar_account_deleted),
                    Snackbar.LENGTH_SHORT
                ).show()
                if (isFiltered) {
                    User.removeAccount(getRealAccountPosition(position))
                    removeAccount(position)
                } else {
                    User.removeAccount(position)
                }
                updateFrom(position)
            }
            .setNegativeButton(context.getString(R.string.dialog_cancel)) { _: DialogInterface, _: Int -> }
            .create().show()
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val company: TextView = view.findViewById(R.id.service)
        val login: TextView = view.findViewById(R.id.login)
        val date: TextView = view.findViewById(R.id.date)
        val copy: MaterialButton = view.findViewById(R.id.copy)
        val viewPassword: MaterialButton = view.findViewById(R.id.see)
        val settings: MaterialButton = view.findViewById(R.id.settings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    onEdit(position)
                }

                R.id.action_delete -> {
                    onDelete(position)
                }
            }
            true
        }

        popup.show()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.company.text = currentDataset[position].service
        holder.login.text = currentDataset[position].login

        holder.settings.setOnClickListener {
            showMenu(it, R.menu.context_menu, position)
        }
        holder.copy.setOnClickListener {
            onCopy(position)
        }

        holder.date.text = currentDataset[position].getLastEdit()

        holder.viewPassword.setOnClickListener {
            onView(position)
        }
    }

    override fun getItemCount(): Int {
        return currentDataset.size
    }

}