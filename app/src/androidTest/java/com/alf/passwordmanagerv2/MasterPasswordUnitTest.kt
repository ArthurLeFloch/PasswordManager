package com.alf.passwordmanagerv2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alf.passwordmanagerv2.security.MasterPassword
import org.junit.Assert.*
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MasterPasswordUnitTest {

    private val password = "password"

    private fun createFile(): File {
        val tmpFolder = TemporaryFolder()
        tmpFolder.create()
        return File("${tmpFolder.root}/test.txt")
    }

    @Test
    fun set_isCorrect() {
        val file = createFile()
        MasterPassword.setFile(file.absolutePath)

        MasterPassword.set(password)

        assertTrue("File does not exist", file.exists())
        assertTrue("File is empty", file.length() > 0)

        val lines = file.readLines()
        assertEquals("Amount of lines is not correct", 2, lines.size)

        assertFalse("Salt contains password", lines[0].contains(password))
        assertFalse("Hash contains password", lines[1].contains(password))

        assertFalse("isEqual() returned true on a wrong password", MasterPassword.isEqual(""))
        assertFalse(
            "isEqual() returned true on a wrong password", MasterPassword.isEqual(password + "1")
        )
        assertFalse(
            "isEqual() returned true on a wrong password", MasterPassword.isEqual("1$password")
        )
        assertFalse(
            "isEqual() returned true on a wrong password",
            MasterPassword.isEqual("1" + password + "1")
        )
        assertTrue("isEqual() returned false on master password", MasterPassword.isEqual(password))
    }

    @Test
    fun get_isCorrect() {
        val file = createFile()
        MasterPassword.setFile(file.absolutePath)

        MasterPassword.set(password)

        assertEquals("get() returned a wrong password", password, MasterPassword.get())
    }

    @Test
    fun exists_isCorrect() {
        val file = createFile()
        MasterPassword.setFile(file.absolutePath)

        assertFalse("exists() returned true on a non-existent file", MasterPassword.exists())

        MasterPassword.set(password)

        assertTrue("exists() returned false on an existent file", MasterPassword.exists())
    }

    @Test
    fun encryption_isCorrect() {
        val file = createFile()
        MasterPassword.setFile(file.absolutePath)

        MasterPassword.set(password)

        val data =
            "Some data on multiple lines\n" + "with special characters\n" + "like é, à, ç, è, _, etc."

        val (encryptedData, salt) = MasterPassword.encrypt(data)
        val decryptedData = MasterPassword.decrypt(encryptedData, salt)

        assertTrue(
            "Decrypted data is not equal to original data", decryptedData.contentEquals(data)
        )

        val data2 = ""

        val (encryptedData2, salt2) = MasterPassword.encrypt(data2)
        val decryptedData2 = MasterPassword.decrypt(encryptedData2, salt2)

        assertTrue(
            "Decrypted data is not equal to original data for empty string",
            decryptedData2.contentEquals(data2)
        )
    }


}