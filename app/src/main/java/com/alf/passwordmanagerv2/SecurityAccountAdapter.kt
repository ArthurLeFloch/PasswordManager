package com.alf.passwordmanagerv2

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class SecurityAccountAdapter(
    private val context: Context, private val root: View, private val dataset: MutableList<Account>
) : RecyclerView.Adapter<SecurityAccountAdapter.ItemViewHolder>() {

    private fun getRealAccountPosition(position: Int): Int {
        return User.dataset.indexOf(dataset[position])
    }

    private fun onEdit(position: Int): Boolean {
        val intent = Intent(context, ChangeAccountPassword::class.java)
        intent.putExtra("id", getRealAccountPosition(position))
        context.startActivity(intent)
        return true
    }

    private fun onCopy(position: Int) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("label", dataset[position].password))
        Snackbar.make(root, "Mot de passe copié !", Snackbar.LENGTH_SHORT).show()
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val company: TextView = view.findViewById(R.id.service)
        val login: TextView = view.findViewById(R.id.login)
        val edit: MaterialButton = view.findViewById(R.id.edit)
        val copy: MaterialButton = view.findViewById(R.id.copy)
        val date: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.security_account_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.company.text = dataset[position].service
        holder.login.text = dataset[position].login

        holder.edit.setOnClickListener {
            onEdit(position)
        }
        holder.copy.setOnClickListener {
            onCopy(position)
        }

        holder.date.text = dataset[position].getLastEdit()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}