package com.alf.passwordmanagerv2.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

fun generateUniqueName(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val randomString = UUID.randomUUID().toString()
    return "$timeStamp-$randomString"
}

fun generatePassword(size: Int): String {
    var allowedChars = ""
    var password = ""
    for (i in 33 until 127) {
        allowedChars += i.toChar()
    }
    for (i in 0 until size) {
        password += allowedChars[Random.nextInt(0, allowedChars.length - 1)]
    }
    return password
}