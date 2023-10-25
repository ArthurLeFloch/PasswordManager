package com.alf.passwordmanagerv2.security

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

private const val ALGORITHM = "AES/GCM/NoPadding"

fun derivedKey(
    password: String, salt: ByteArray? = null
): Pair<SecretKeySpec, ByteArray> {
    val newSalt = salt ?: Random.nextBytes(64)
    val spec = PBEKeySpec(password.toCharArray(), newSalt, 2048, 256)
    val secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").generateSecret(spec)
    return Pair(SecretKeySpec(secret.encoded, "AES"), newSalt)
}

fun encrypt(data: String, derivedKey: SecretKeySpec): ByteArray {
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, derivedKey, IvParameterSpec(ByteArray(16)))
    return cipher.doFinal(data.toByteArray())
}

fun encrypt(data: String, password: String): Pair<ByteArray, ByteArray> {
    val (key, salt) = derivedKey(password)
    return Pair(encrypt(data, key), salt)
}

fun decrypt(data: ByteArray, derivedKey: SecretKeySpec): String {
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, derivedKey, IvParameterSpec(ByteArray(16)))
    return String(cipher.doFinal(data))
}

fun decrypt(encryptedData: ByteArray, password: String, salt: ByteArray): String {
    return decrypt(encryptedData, derivedKey(password, salt).first)
}