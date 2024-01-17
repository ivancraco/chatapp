package com.ivandev.chatapp.data.cipher

import java.security.MessageDigest
import javax.crypto.spec.SecretKeySpec

object Key {
    fun key(text: String): SecretKeySpec {
        val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
        var key = text.encodeToByteArray()
        key = sha.digest(key)
        return SecretKeySpec(key, "AES")
    }
}