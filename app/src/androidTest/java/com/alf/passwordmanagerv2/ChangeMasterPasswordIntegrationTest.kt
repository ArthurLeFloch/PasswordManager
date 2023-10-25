package com.alf.passwordmanagerv2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alf.passwordmanagerv2.security.MasterPassword
import org.junit.Assert.*
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ChangeMasterPasswordIntegrationTest {

    private fun createFolder(): File {
        val tmpFolder = TemporaryFolder()
        tmpFolder.create()
        return File(tmpFolder.root.toString())
    }

    @Test
    fun changePassword_isCorrect() {
        val folder = createFolder()
        val identityFile = File("${folder.absolutePath}/identityFile")
        MasterPassword.setFile(identityFile.absolutePath)

        User.init(folder.absolutePath)

        MasterPassword.set("password")

        User.createAccount("service0", "login0", "0password0")
        User.createAccount("service1", "login1", "1password1")
        User.createAccount("service2", "login2", "2password2")

        User.changeMasterPassword("newPassword")

        assertEquals("Master password is not correct", "newPassword", MasterPassword.get())
        assertEquals("Amount of accounts is not correct", 3, User.dataset.size)

        assertEquals("Account 0 service is not correct", "service0", User.dataset[0].service)
        assertEquals("Account 0 login is not correct", "login0", User.dataset[0].login)
        assertEquals(
            "Account 0 password is not correct",
            "0password0",
            User.dataset[0].getPassword()
        )

        assertEquals(
            "Account 1 password is not correct",
            "1password1",
            User.dataset[1].getPassword()
        )

        assertEquals(
            "Account 2 password is not correct",
            "2password2",
            User.dataset[2].getPassword()
        )

        User.changeAccountPassword(1, "1newPassword1")

        User.changeMasterPassword("brandNewPassword")

        assertEquals("Master password is not correct", "brandNewPassword", MasterPassword.get())
        assertEquals("Amount of accounts is not correct", 3, User.dataset.size)

        assertEquals("Account 0 service is not correct", "service0", User.dataset[0].service)
        assertEquals("Account 0 login is not correct", "login0", User.dataset[0].login)
        assertEquals(
            "Account 0 password is not correct",
            "0password0",
            User.dataset[0].getPassword()
        )

        assertEquals(
            "Account 1 password is not correct",
            "1newPassword1",
            User.dataset[1].getPassword()
        )

        assertEquals(
            "Account 2 password is not correct",
            "2password2",
            User.dataset[2].getPassword()
        )
    }

}