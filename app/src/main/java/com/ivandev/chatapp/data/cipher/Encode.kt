package com.ivandev.chatapp.data.cipher

import android.graphics.Bitmap
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.ivandev.chatapp.data.cipher.Key.key
import java.io.ByteArrayOutputStream

object Encode {
    fun encodeMessage(text: String, pass: String): String {
        val secretKey: SecretKeySpec = key(pass)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        val encryptedDataBytes = cipher.doFinal(text.encodeToByteArray())
        return Base64.encodeToString(encryptedDataBytes, Base64.DEFAULT)
    }

    fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}