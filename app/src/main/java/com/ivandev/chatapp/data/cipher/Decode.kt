package com.ivandev.chatapp.data.cipher

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.ivandev.chatapp.data.cipher.Key.key
import java.io.ByteArrayInputStream

object Decode {
    fun decodeMessage(text: String, pass: String): String {
        val secretKey: SecretKeySpec = key(pass)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        val decodedData = Base64.decode(text, Base64.DEFAULT)
        val desencryptedDataBytes = cipher.doFinal(decodedData)
        return String(desencryptedDataBytes)
    }

    fun decodeImage(image: String): ImageBitmap {
        val stream =
            ByteArrayInputStream(Base64.decode(image.toByteArray(Charsets.UTF_8), Base64.DEFAULT))
        return BitmapFactory.decodeStream(stream).asImageBitmap()
    }
}