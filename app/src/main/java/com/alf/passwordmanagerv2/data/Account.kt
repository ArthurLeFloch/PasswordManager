package com.alf.passwordmanagerv2.data

import com.alf.passwordmanagerv2.utils.bytesToStr
import com.alf.passwordmanagerv2.utils.findField
import com.alf.passwordmanagerv2.utils.generateUniqueName
import com.alf.passwordmanagerv2.utils.saveFields
import com.alf.passwordmanagerv2.utils.strToBytes
import java.io.File
import java.util.Date


class Account(
    val path: String,
    var service: String,
    var login: String,
    private var encryptedPassword: ByteArray,
    private var passwordSalt: ByteArray,
    var date: Date = Date()
) {

    companion object {
        fun load(filePath: String): Account {
            val service = findField("Service", filePath)!!
            val login = findField("Login", filePath)!!
            val passwordSalt = strToBytes(findField("Salt", filePath)!!)
            val encryptedPassword = strToBytes(findField("Hash", filePath)!!)
            val date = Security.dateFormat.parse(findField("Date", filePath)!!)!!

            return Account(filePath, service, login, encryptedPassword, passwordSalt, date)
        }

        fun create(path: String, service: String, login: String, password: String): Account {
            val fileName = generateUniqueName()
            val filePath = "$path/${fileName}"
            val (encrypted, salt) = MasterPassword.encrypt(password)
            return Account(filePath, service, login, encrypted, salt)
        }
    }

    fun save() {
        val saltString = bytesToStr(passwordSalt)
        val passwordString = bytesToStr(encryptedPassword)
        val dateString = Security.dateFormat.format(date)

        val servicePair = Pair("Service", service)
        val loginPair = Pair("Login", login)
        val saltPair = Pair("Salt", saltString)
        val passwordPair = Pair("Hash", passwordString)
        val datePair = Pair("Date", dateString)

        saveFields(listOf(servicePair, loginPair, saltPair, passwordPair, datePair), path)
    }

    fun getLastEdit(): String {
        return Security.dateFormat.format(date)
    }

    fun setPassword(password: String, resetDate: Boolean = true) {
        val (encrypted, salt) = MasterPassword.encrypt(password)
        encryptedPassword = encrypted
        passwordSalt = salt
        if (resetDate) {
            date = Date()
        }
        save()
    }

    fun getPassword(): String {
        return MasterPassword.decrypt(encryptedPassword, passwordSalt)
    }

    fun delete() {
        File(path).delete()
    }
}