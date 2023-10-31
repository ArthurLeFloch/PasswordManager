package com.alf.passwordmanagerv2

import com.alf.passwordmanagerv2.utils.generatePassword
import com.alf.passwordmanagerv2.utils.generateUniqueName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GenerateUnitTest {

    @Test
    fun generateUniqueName_isCorrect() {
        val fileNames = mutableListOf<String>()
        val amount = 1000
        for (i in 0 until amount) {
            fileNames.add(generateUniqueName())
        }
        for (i in 0 until amount) {
            for (j in i + 1 until amount) {
                assertNotEquals("Generated names are not unique", fileNames[i], fileNames[j])

            }
        }
    }

    @Test
    fun generatePassword_isCorrect() {
        var password = generatePassword(10)
        assertEquals("Password length is not correct", 10, password.length)

        password = generatePassword(20)
        assertEquals("Password length is not correct", 20, password.length)

        password = generatePassword(30)
        assertEquals("Password length is not correct", 30, password.length)

        val passwords = mutableListOf<String>()
        val amount = 1000
        for (i in 0 until amount) {
            passwords.add(generatePassword(20))
        }
        for (i in 0 until amount) {
            for (j in i + 1 until amount) {
                assertNotEquals("Generated passwords are not unique", passwords[i], passwords[j])
            }
        }
    }

}