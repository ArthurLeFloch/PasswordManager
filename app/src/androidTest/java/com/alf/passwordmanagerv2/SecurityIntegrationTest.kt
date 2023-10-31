package com.alf.passwordmanagerv2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.alf.passwordmanagerv2.data.MasterPassword
import com.alf.passwordmanagerv2.data.Security
import org.junit.Assert.*
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SecurityIntegrationTest {

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

        Security.init(getInstrumentation().targetContext, folder.absolutePath)

        Security.setMasterPassword("password")

        Security.createAccount("service0", "login0", "0password0")
        Security.createAccount("service1", "login1", "1password1")
        Security.createAccount("service2", "login2", "2password2")

        Security.updateMasterPassword("newPassword")

        var accounts = Security.getAccounts()

        var index0 = accounts.indexOfFirst { it.service == "service0" }
        var index1 = accounts.indexOfFirst { it.service == "service1" }
        var index2 = accounts.indexOfFirst { it.service == "service2" }

        assertTrue("Master password is not correct", Security.isMasterPassword("newPassword"))
        assertEquals("Amount of accounts is not correct", 3, accounts.size)

        assertEquals(
            "Account 0 service is not correct", "service0", accounts[index0].service
        )
        assertEquals("Account 0 login is not correct", "login0", accounts[index0].login)
        assertEquals(
            "Account 0 password is not correct",
            "0password0",
            accounts[index0].getPassword()
        )

        assertEquals(
            "Account 1 password is not correct",
            "1password1",
            accounts[index1].getPassword()
        )

        assertEquals(
            "Account 2 password is not correct",
            "2password2",
            accounts[index2].getPassword()
        )

        Security.changeAccountPassword(accounts[index1], "1newPassword1")

        Security.updateMasterPassword("brandNewPassword")

        accounts = Security.getAccounts()

        index0 = accounts.indexOfFirst { it.service == "service0" }
        index1 = accounts.indexOfFirst { it.service == "service1" }
        index2 = accounts.indexOfFirst { it.service == "service2" }

        assertTrue("Master password is not correct", Security.isMasterPassword("brandNewPassword"))
        assertEquals("Amount of accounts is not correct", 3, accounts.size)

        assertEquals(
            "Account 0 service is not correct", "service0", accounts[index0].service
        )
        assertEquals("Account 0 login is not correct", "login0", accounts[index0].login)
        assertEquals(
            "Account 0 password is not correct",
            "0password0",
            accounts[index0].getPassword()
        )

        assertEquals(
            "Account 1 password is not correct",
            "1newPassword1",
            accounts[index1].getPassword()
        )

        assertEquals(
            "Account 2 password is not correct",
            "2password2",
            accounts[index2].getPassword()
        )
    }

}