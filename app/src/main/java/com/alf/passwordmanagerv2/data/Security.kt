package com.alf.passwordmanagerv2.data

import android.content.Context
import com.alf.passwordmanagerv2.R
import java.io.File
import java.text.SimpleDateFormat

object Security {
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

    fun getAccounts(): List<Account> {
        val dataset = mutableListOf<Account>()
        val files = File(accountsPath).listFiles()!!
        for (i in files.indices) {
            dataset.add(Account.load(files[i].absolutePath))
        }
        return dataset
    }

    fun getAccount(path: String): Account {
        return Account.load(path)
    }

    fun setMasterPassword(password: String) {
        MasterPassword.set(password)
    }

    fun updateMasterPassword(password: String) {
        val accounts = getAccounts()
        val count = accounts.size

        fun getPassword(i: Int): String {
            return accounts[i].getPassword()
        }

        fun setPassword(i: Int, password: String) {
            accounts[i].setPassword(password, resetDate = false)
        }

        MasterPassword.updateEncryption(password, count, ::getPassword, ::setPassword)
    }

    fun isMasterPassword(password: String): Boolean {
        return MasterPassword.isEqual(password)
    }

    fun changeAccountPassword(account: Account, newPassword: String) {
        account.setPassword(newPassword)
    }

    fun createAccount(service: String, login: String, password: String): Account {
        val account = Account.create(accountsPath, service, login, password)
        account.save()
        return account
    }

    fun clear() {
        MasterPassword.clear()
    }
}