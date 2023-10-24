package com.alf.passwordmanagerv2

import android.os.Handler
import android.os.Looper
import com.alf.passwordmanagerv2.security.MasterPassword
import java.io.File

object User {
    val dataset = mutableListOf<Account>()

    private lateinit var accountsPath: String

    var theme: Int = -1

    fun init(path: String) {
        MasterPassword.setFile("$path/identityFile")

        accountsPath = "$path/storedData"

        val accountsFolder = File(accountsPath)
        if (!accountsFolder.exists()) {
            accountsFolder.mkdir()
        }
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

    fun saveAccounts() {
        Account.saveAll(dataset)
    }

    fun changeAccountPassword(id: Int, newPassword: String) {
        dataset[id].password = newPassword
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