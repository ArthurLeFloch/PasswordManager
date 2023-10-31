package com.alf.passwordmanagerv2.utils

import java.io.File

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