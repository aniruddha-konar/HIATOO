package com.example.hiatoo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.hiatoo.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    private val _geminiApiKey = MutableStateFlow(repository.getGeminiApiKey() ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _elevenLabsApiKey = MutableStateFlow(repository.getElevenLabsApiKey() ?: "")
    val elevenLabsApiKey: StateFlow<String> = _elevenLabsApiKey.asStateFlow()

    private val _voiceId = MutableStateFlow(repository.getVoiceId())
    val voiceId: StateFlow<String> = _voiceId.asStateFlow()

    private val _speechSpeed = MutableStateFlow(repository.getSpeechSpeed())
    val speechSpeed: StateFlow<Float> = _speechSpeed.asStateFlow()

    private val _darkMode = MutableStateFlow(repository.isDarkMode())
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    fun updateGeminiApiKey(key: String) {
        _geminiApiKey.value = key
        repository.saveGeminiApiKey(key)
    }

    fun updateElevenLabsApiKey(key: String) {
        _elevenLabsApiKey.value = key
        repository.saveElevenLabsApiKey(key)
    }

    fun updateVoiceId(id: String) {
        _voiceId.value = id
        repository.saveVoiceId(id)
    }

    fun updateSpeechSpeed(speed: Float) {
        _speechSpeed.value = speed
        repository.saveSpeechSpeed(speed)
    }

    fun updateDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
        repository.saveDarkMode(enabled)
    }
}
