package com.alf.passwordmanagerv2

import com.alf.passwordmanagerv2.utils.bytesToStr
import com.alf.passwordmanagerv2.utils.strToBytes
import org.junit.Assert.*
import org.junit.Test

class ConversionUnitTest {

    @Test
    fun byteStringConversion_isCorrect() {
        val text = "01234567890ABCDEFFEDCBA09876543210"
        val bytes = strToBytes(text)
        val string = bytesToStr(bytes)
        assertEquals("String is not equal to original string", text, string)
    }
}