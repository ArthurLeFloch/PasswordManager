package com.alf.passwordmanagerv2

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun bytesToStr(byteArray: ByteArray): String {
    return byteArray.joinToString("") { String.format("%02x", it) }.uppercase()
}

fun strToBytes(string: String): ByteArray {
    return string.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun saveFields(fields: List<Pair<String, String>>, path: String) {
    val file = File(path)
    file.delete()
    file.createNewFile()
    for (field in fields) {
        file.appendText("${field.first}: ${field.second}\n")
    }
}

fun getFields(path: String): List<Pair<String, String>> {
    val file = File(path)
    val lines = file.readLines()
    return lines.map { it.split(": ") }.map { Pair(it[0], it[1]) }
}

fun findField(field: String, path: String): String? {
    val fields = getFields(path)
    for (f in fields) {
        if (f.first == field) {
            return f.second
        }
    }
    return null
}

fun generateUniqueName(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val randomString = UUID.randomUUID().toString()
    return "$timeStamp-$randomString"
}