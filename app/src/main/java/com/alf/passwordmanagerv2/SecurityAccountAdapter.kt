package com.alf.passwordmanagerv2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alf.passwordmanagerv2.data.Account
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class SecurityAccountAdapter(
    private val context: Context,
    private val root: View,
    private val dataset: MutableList<Account>
) : RecyclerView.Adapter<SecurityAccountAdapter.ItemViewHolder>() {

    private fun onEdit(position: Int): Boolean {
        val intent = Intent(context, EditAccountPassword::class.java)
        intent.putExtra("path", dataset[position].path)
        context.startActivity(intent)
        return true
    }

    private fun onCopy(position: Int) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", dataset[position].getPassword()))
        Snackbar.make(
            root, context.getString(R.string.snackbar_password_copied), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onView(position: Int) {
        val intent = Intent(context, ShowPassword::class.java)
        intent.putExtra("path", dataset[position].path)
        context.startActivity(intent)
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val company: TextView = view.findViewById(R.id.service)
        val login: TextView = view.findViewById(R.id.login)
        val date: TextView = view.findViewById(R.id.date)
        val copy: MaterialButton = view.findViewById(R.id.copy)
        val viewPassword: MaterialButton = view.findViewById(R.id.see)
        val edit: MaterialButton = view.findViewById(R.id.edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.security_account_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.company.text = dataset[position].service
        holder.login.text = dataset[position].login
        holder.date.text = dataset[position].getLastEdit()

        holder.copy.setOnClickListener {
            onCopy(position)
        }
        holder.viewPassword.setOnClickListener {
            onView(position)
        }
        holder.edit.setOnClickListener {
            onEdit(position)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}