package com.alf.passwordmanagerv2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alf.passwordmanagerv2.security.decrypt
import com.alf.passwordmanagerv2.security.derivedKey
import com.alf.passwordmanagerv2.security.encrypt
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionInstrumentalTest {

    private val password = "password"
    private val data = "data"

    @Test
    fun encryptionWithString_isCorrect() {
        val (encryptedData, salt) = encrypt(data, password)
        val decryptedData = decrypt(encryptedData, password, salt)
        assertEquals("Decrypted data is not equal to original data", data, decryptedData)
    }

    @Test
    fun encryptionWithKey_isCorrect() {
        val derivedKey = derivedKey(password).first

        val encryptedData = encrypt(data, derivedKey)
        val decryptedData = decrypt(encryptedData, derivedKey)

        assertEquals("Decrypted data is not equal to original data", data, decryptedData)
    }

}