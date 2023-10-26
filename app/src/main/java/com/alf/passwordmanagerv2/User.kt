package com.alf.passwordmanagerv2

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alf.passwordmanagerv2.security.MasterPassword
import java.io.File
import java.text.SimpleDateFormat

object User {
    val dataset = mutableListOf<Account>()

    private lateinit var accountsPath: String
    lateinit var dateFormat: SimpleDateFormat

    fun init(context: Context, path: String) {
        MasterPassword.setFile("$path/identityFile")

        accountsPath = "$path/storedData"

        val accountsFolder = File(accountsPath)
        if (!accountsFolder.exists()) {
            accountsFolder.mkdir()
        }

        dateFormat = SimpleDateFormat(context.getString(R.string.datetime_format))
    }

    fun isFirstLog(): Boolean {
        return !MasterPassword.exists()
    }

    fun loadAccounts(onInit: (Int) -> Unit, onUpdate: (Int) -> Unit, onFinish: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        dataset.clear()
        val files = File(accountsPath).listFiles()!!
        handler.post {
            onInit(files.size)
        }
        for (i in files.indices) {
            val account = Account.load(files[i].absolutePath)
            dataset.add(account)
            handler.post {
                onUpdate(i + 1)
            }
        }
        handler.post {
            onFinish()
        }
    }

    fun changeMasterPassword(password: String) {
        MasterPassword.updateEncryption(password, dataset)
    }

    fun changeAccountPassword(id: Int, newPassword: String) {
        dataset[id].setPassword(newPassword)
        dataset[id].save()
    }

    fun getAccount(id: Int): Account {
        return dataset[id]
    }

    fun removeAccount(id: Int) {
        dataset[id].remove()
        dataset.removeAt(id)
    }

    fun createAccount(service: String, login: String, password: String) {
        val account = Account.createAccount(accountsPath, service, login, password)
        dataset.add(account)
        account.save()
    }

    fun clear() {
        dataset.clear()
        MasterPassword.clear()
    }
}