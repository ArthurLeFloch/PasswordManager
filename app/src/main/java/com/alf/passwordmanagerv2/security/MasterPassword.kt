package com.alf.passwordmanagerv2.security

import com.alf.passwordmanagerv2.bytesToStr
import com.alf.passwordmanagerv2.findField
import com.alf.passwordmanagerv2.saveFields
import com.alf.passwordmanagerv2.strToBytes
import java.io.File
import java.security.MessageDigest
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object MasterPassword {

    private var file: String? = null

    private val decryptingKey: SecretKeySpec = derivedKey("PasswordManager").first
    private lateinit var encryptedPassword: ByteArray

    fun setFile(path: String) {
        file = path
    }

    private fun hash(
        password: String, salt: ByteArray? = null
    ): Pair<ByteArray, ByteArray> {
        val newSalt = salt ?: Random.nextBytes(64)
        val md = MessageDigest.getInstance("SHA-512")
        md.update(newSalt)
        val hash = md.digest(password.toByteArray())
        return Pair(hash, newSalt)
    }

    fun set(password: String) {
        val (hash, salt) = hash(password)
        val saltPair = Pair("Salt", bytesToStr(salt))
        val hashPair = Pair("Hash", bytesToStr(hash))
        encryptedPassword = encrypt(password, decryptingKey)
        saveFields(listOf(saltPair, hashPair), file!!)
    }

    fun get(): String {
        return decrypt(encryptedPassword, decryptingKey)
    }

    fun encrypt(data: String): Pair<ByteArray, ByteArray> {
        return encrypt(data, get())
    }

    fun decrypt(data: ByteArray, salt: ByteArray): String {
        return decrypt(data, derivedKey(get(), salt).first)
    }

    fun exists(): Boolean {
        if (file == null) {
            return false
        }
        if (!File(file!!).exists()) {
            return false
        }
        if (findField("Salt", file!!) == null || findField("Hash", file!!) == null) {
            return false
        }
        return true
    }

    fun isEqual(password: String): Boolean {
        val salt = strToBytes(findField("Salt", file!!)!!)
        val hash = strToBytes(findField("Hash", file!!)!!)
        return hash.contentEquals(hash(password, salt).first)
    }

    fun clear() {
        encryptedPassword = ByteArray(0)
    }
}