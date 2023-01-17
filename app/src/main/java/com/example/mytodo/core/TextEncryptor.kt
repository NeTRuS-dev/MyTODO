package com.example.mytodo.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.Key
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class TextEncryptor @Inject constructor(@ApplicationContext context: Context) {
    private val key: Key by lazy {
        SecretKeySpec(context.packageName.toByteArray(), "AES")
    }

    fun encrypt(text: String): String {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        val cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(text.toByteArray())
        return encrypted.toString()
    }

    fun decrypt(text: String): String {
        val cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(text.toByteArray())
        return decrypted.toString()
    }
}