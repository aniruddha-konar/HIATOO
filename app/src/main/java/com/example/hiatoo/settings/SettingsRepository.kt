package com.example.hiatoo.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SettingsRepository(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "hiatoo_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun sanitizeKey(key: String?): String? {
        return key?.replace("\"", "")?.replace("'", "")?.replace(" ", "")?.trim()
    }

    fun saveGeminiApiKey(key: String) {
        sharedPreferences.edit().putString("gemini_api_key", sanitizeKey(key)).apply()
    }

    fun getGeminiApiKey(): String? {
        return sanitizeKey(sharedPreferences.getString("gemini_api_key", null))
    }

    fun saveElevenLabsApiKey(key: String) {
        sharedPreferences.edit().putString("elevenlabs_api_key", sanitizeKey(key)).apply()
    }

    fun getElevenLabsApiKey(): String? {
        return sanitizeKey(sharedPreferences.getString("elevenlabs_api_key", null))
    }

    fun saveVoiceId(id: String) {
        sharedPreferences.edit().putString("voice_id", id).apply()
    }

    fun getVoiceId(): String {
        return sharedPreferences.getString("voice_id", "EXAVITQu4vr4xnSDxMaL") ?: "EXAVITQu4vr4xnSDxMaL"
    }

    fun saveSpeechSpeed(speed: Float) {
        sharedPreferences.edit().putFloat("speech_speed", speed).apply()
    }

    fun getSpeechSpeed(): Float {
        return sharedPreferences.getFloat("speech_speed", 1.0f)
    }

    fun saveDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", true)
    }
}
