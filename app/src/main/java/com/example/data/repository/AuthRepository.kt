package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.CoachDao
import com.example.data.model.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom

sealed class AuthResult {
    data class Success(val username: String, val role: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Local, offline-first authentication backed by Room. Passwords are stored as
 * SHA-256(salt + password) — never in plain text. The session lives in
 * SharedPreferences so the user stays logged in across app restarts.
 *
 * A default admin account (admin / admin123) is seeded on first launch and
 * should be changed after the first login in a real deployment.
 */
class AuthRepository(
    context: Context,
    private val dao: CoachDao
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("speakora_auth", Context.MODE_PRIVATE)

    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_USER = "USER"
        const val DEFAULT_ADMIN_USERNAME = "admin"
        const val DEFAULT_ADMIN_PASSWORD = "admin123"
        private const val KEY_SESSION_USER = "session_username"
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun newSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    suspend fun ensureAdminSeeded() = withContext(Dispatchers.IO) {
        if (dao.countAdmins() == 0) {
            val salt = newSalt()
            dao.insertUserAccount(
                UserAccount(
                    username = DEFAULT_ADMIN_USERNAME,
                    passwordHash = hash(DEFAULT_ADMIN_PASSWORD, salt),
                    salt = salt,
                    role = ROLE_ADMIN
                )
            )
        }
    }

    suspend fun login(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val clean = username.trim()
        if (clean.isEmpty() || password.isEmpty()) return@withContext AuthResult.Error("Please fill in both fields")
        val account = dao.getUserAccount(clean)
            ?: return@withContext AuthResult.Error("No account found for \"$clean\" — sign up first")
        if (account.passwordHash != hash(password, account.salt)) {
            return@withContext AuthResult.Error("Incorrect password")
        }
        prefs.edit().putString(KEY_SESSION_USER, account.username).apply()
        AuthResult.Success(account.username, account.role)
    }

    suspend fun signup(username: String, password: String, asAdmin: Boolean = false): AuthResult = withContext(Dispatchers.IO) {
        val clean = username.trim()
        if (clean.length < 3) return@withContext AuthResult.Error("Username must be at least 3 characters")
        if (clean.contains(" ")) return@withContext AuthResult.Error("Username cannot contain spaces")
        if (password.length < 4) return@withContext AuthResult.Error("Password must be at least 4 characters")
        if (dao.getUserAccount(clean) != null) return@withContext AuthResult.Error("That username is already taken")
        val salt = newSalt()
        val account = UserAccount(
            username = clean,
            passwordHash = hash(password, salt),
            salt = salt,
            role = if (asAdmin) ROLE_ADMIN else ROLE_USER
        )
        dao.insertUserAccount(account)
        prefs.edit().putString(KEY_SESSION_USER, account.username).apply()
        AuthResult.Success(account.username, account.role)
    }

    fun currentSession(): Pair<String, String>? {
        val username = prefs.getString(KEY_SESSION_USER, null) ?: return null
        // Role is resolved lazily by the ViewModel via lookup; a cheap sync read
        // is not possible with suspend DAO, so store role alongside at login.
        val role = prefs.getString("session_role", null)
        return if (role != null) username to role else null
    }

    fun saveSession(username: String, role: String) {
        prefs.edit()
            .putString(KEY_SESSION_USER, username)
            .putString("session_role", role)
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    suspend fun allAccounts(): List<UserAccount> = withContext(Dispatchers.IO) { dao.getAllUserAccounts() }
}
