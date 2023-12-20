package com.example.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.util.*

@kotlinx.serialization.Serializable
data class UserCredentials(val username: String, val password: String)

fun UserCredentials.hashedPassword(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val bytes = messageDigest.digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun UserCredentials.generateToken() = JWT.create()
    .withSubject(System.getenv("subject"))
    .withIssuer(System.getenv("issuer"))
    .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 10)) //10 hours
    .withClaim("username", username)
    .withClaim("password", password)
    .sign(Algorithm.HMAC256(System.getenv("secretKey")))