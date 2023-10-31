package com.alf.passwordmanagerv2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alf.passwordmanagerv2.data.decrypt
import com.alf.passwordmanagerv2.data.derivedKey
import com.alf.passwordmanagerv2.data.encrypt
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionUnitTest {

    private val password = "password"
    private val data = "data"

    @Test
    fun encryption_isCorrect() {
        val derivedKey = derivedKey(password).first

        val encryptedData = encrypt(data, derivedKey)
        val decryptedData = decrypt(encryptedData, derivedKey)

        assertEquals("Decrypted data is not equal to original data", data, decryptedData)
    }

}