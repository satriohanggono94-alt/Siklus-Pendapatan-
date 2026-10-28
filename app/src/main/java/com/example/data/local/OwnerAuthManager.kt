package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OwnerAuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("owner_auth_prefs", Context.MODE_PRIVATE)

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    companion object {
        private const val KEY_OWNER_PASSWORD = "owner_password_hash"
        const val DEFAULT_PASSWORD = "owner2026"
    }

    private fun getStoredPassword(): String {
        return prefs.getString(KEY_OWNER_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
    }

    fun verifyAndUnlock(passwordInput: String): Boolean {
        val stored = getStoredPassword()
        val isValid = passwordInput.trim() == stored.trim()
        if (isValid) {
            _isUnlocked.value = true
        }
        return isValid
    }

    fun lock() {
        _isUnlocked.value = false
    }

    fun changePassword(oldPass: String, newPass: String): Boolean {
        if (verifyAndUnlock(oldPass) && newPass.isNotBlank()) {
            prefs.edit().putString(KEY_OWNER_PASSWORD, newPass.trim()).apply()
            return true
        }
        return false
    }
}
