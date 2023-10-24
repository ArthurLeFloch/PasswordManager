package com.alf.passwordmanagerv2

import com.alf.passwordmanagerv2.security.MasterPassword
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")

class Account(
    private val path: String, var service: String, var login: String, password: String, var date: Date = Date()
) {

    companion object {
        fun load(filePath: String): Account {
            val service = findField("Service", filePath)!!
            val login = findField("Login", filePath)!!
            val salt = strToBytes(findField("Salt", filePath)!!)
            val encryptedPassword = strToBytes(findField("Hash", filePath)!!)
            val date = dateFormat.parse(findField("Date", filePath)!!)!!

            val accountPassword = MasterPassword.decrypt(encryptedPassword, salt)
            return Account(filePath, service, login, accountPassword, date)
        }

        fun createAccount(path: String, service: String, login: String, password: String): Account {
            val fileName = generateUniqueName()
            val filePath = "$path/${fileName}"
            return Account(filePath, service, login, password)
        }

        fun saveAll(accounts: MutableList<Account>) {
            for (account in accounts) {
                account.save()
            }
        }
    }

    var password: String = password
        set(value) {
            field = value
            date = Date()
        }

    fun save() {
        val (encryptedPassword, salt) = MasterPassword.encrypt(password)

        val saltString = bytesToStr(salt)
        val passwordString = bytesToStr(encryptedPassword)
        val dateString = dateFormat.format(date)

        val servicePair = Pair("Service", service)
        val loginPair = Pair("Login", login)
        val saltPair = Pair("Salt", saltString)
        val passwordPair = Pair("Hash", passwordString)
        val datePair = Pair("Date", dateString)

        saveFields(listOf(servicePair, loginPair, saltPair, passwordPair, datePair), path)
    }

    fun getLastEdit(): String {
        return dateFormat.format(date)
    }

    fun hasChangedSince(newDate: Date): Boolean {
        return date.before(newDate)
    }

    fun remove() {
        File(path).delete()
    }
}