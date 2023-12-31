package com.alf.passwordmanagerv2.utils

import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import com.alf.passwordmanagerv2.R
import com.alf.passwordmanagerv2.data.Account
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.security.MessageDigest
import java.util.Date
import java.util.concurrent.Executors

private fun sha1(password: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    val hash = md.digest(password.toByteArray())
    return bytesToStr(hash)
}

// Raises an exception if no internet connection or weak connection
private fun useCountRequest(prefix: String): List<List<String>> {
    val url = "https://api.pwnedpasswords.com/range/$prefix"
    val timeout = 750

    val connection = URL(url).openConnection()

    connection.connectTimeout = timeout
    connection.readTimeout = timeout

    connection.connect()

    val inputStream = connection.getInputStream()
    val reader = BufferedReader(InputStreamReader(inputStream))
    val response = reader.readText()

    reader.close()
    inputStream.close()

    return response.split("\n").map { it.split(":").map { it.trim() } }
}

private fun useCount(password: String): Int {
    val passwordHash = sha1(password)
    val prefix = passwordHash.substring(0, 5)
    val suffix = passwordHash.substring(5)

    val hashList = useCountRequest(prefix)

    var left = 0
    var right = hashList.size - 1

    while (left <= right) {
        val mid = (left + right) / 2
        val (hash, count) = hashList[mid]
        if (hash == suffix) {
            return count.toInt()
        } else if (hash < suffix) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return 0
}

fun accountsToChange(accounts: List<Account>, days: Int): List<Account> {
    val result = accounts.toMutableList()
    var i = 0
    val currentTime = Date().time
    val divider = 24 * 60 * 60 * 1000
    while (i < result.size) {
        val diff = currentTime - result[i].date.time
        val diffDays = diff / divider
        if (diffDays < days) {
            result.removeAt(i)
        } else {
            i++
        }
    }
    result.sortBy { it.date }
    return result

}

fun searchPassword(context: Context, password: String, onResult: (Boolean) -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(context.mainLooper)
    executor.execute {
        try {
            val count = useCount(password)
            handler.post {
                if (count > 0) {
                    val formattedCount = String.format("%,d", count)
                    MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.alert_security))
                        .setMessage(
                            String.format(
                                context.getString(R.string.password_already_found_text_info),
                                formattedCount
                            )
                        )
                        .setPositiveButton(context.getString(R.string.yes)) { _: DialogInterface, _: Int ->
                            onResult(true)
                        }
                        .setNegativeButton(context.getString(R.string.no)) { _: DialogInterface, _: Int ->
                            onResult(false)
                        }.show()
                } else if (count == 0) {
                    onResult(true)
                }
            }
        } catch (e: Exception) {
            handler.post {
                MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.no_internet))
                    .setMessage(context.getString(R.string.password_not_checked))
                    .setPositiveButton(context.getString(R.string.yes)) { _: DialogInterface, _: Int ->
                        onResult(true)
                    }
                    .setNegativeButton(context.getString(R.string.no)) { _: DialogInterface, _: Int ->
                        onResult(false)
                    }.show()
            }
        }

    }
}