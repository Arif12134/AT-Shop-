package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Model representing an authenticated Admin / Website Owner session.
 */
data class AdminSession(
    val username: String = "ariful",
    val role: String = "Website Owner / Super Admin",
    val sessionToken: String = "sess_${UUID.randomUUID().toString().take(12)}",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (2 * 60 * 60 * 1000L), // 2 hours
    val ipAddress: String = "127.0.0.1 (Local / Container Secured)"
) {
    val isValid: Boolean
        get() = System.currentTimeMillis() < expiresAt

    val formattedExpiry: String
        get() {
            val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
            return sdf.format(Date(expiresAt))
        }

    val formattedLoginTime: String
        get() {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date(createdAt))
        }
}

/**
 * Structured result of an admin authentication attempt.
 */
sealed class AdminLoginResult {
    data class Success(val session: AdminSession) : AdminLoginResult()
    data class Error(
        val message: String,
        val field: LoginField? = null,
        val remainingAttempts: Int = 5,
        val isLockedOut: Boolean = false
    ) : AdminLoginResult()
}

enum class LoginField {
    USERNAME,
    PASSWORD,
    GENERAL
}
