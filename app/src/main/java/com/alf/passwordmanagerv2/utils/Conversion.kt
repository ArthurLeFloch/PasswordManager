package com.alf.passwordmanagerv2.utils

fun bytesToStr(byteArray: ByteArray): String {
    return byteArray.joinToString("") { String.format("%02x", it) }.uppercase()
}

fun strToBytes(string: String): ByteArray {
    return string.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}