package com.example.hiatoo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiatoo.BuildConfig
import com.example.hiatoo.ai.GeminiService
import com.example.hiatoo.models.AIFaceState
import com.example.hiatoo.voice.ElevenLabsService
import com.example.hiatoo.voice.VoiceRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _faceState = MutableStateFlow(AIFaceState.IDLE)
    val faceState: StateFlow<AIFaceState> = _faceState.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()
    
    private val _aiResponseText = MutableStateFlow("")
    val aiResponseText: StateFlow<String> = _aiResponseText.asStateFlow()

    private val _errorText = MutableStateFlow("")
    val errorText: StateFlow<String> = _errorText.asStateFlow()

    private var voiceRecognizer: VoiceRecognizer? = null
    
    private val settingsRepository = com.example.hiatoo.settings.SettingsRepository(application)
    
    // Load API Keys initially
    private var geminiService = GeminiService(
        settingsRepository.getGeminiApiKey()?.takeIf { it.isNotBlank() } ?: BuildConfig.GEMINI_API_KEY
    )
    private var elevenLabsService = ElevenLabsService(
        application, 
        settingsRepository.getElevenLabsApiKey()?.takeIf { it.isNotBlank() } ?: BuildConfig.ELEVENLABS_API_KEY
    )

    init {
        voiceRecognizer = VoiceRecognizer(
            context = application,
            onResult = { text ->
                _spokenText.value = text
                processVoiceInput(text)
            },
            onError = { err ->
                _errorText.value = err
                _faceState.value = AIFaceState.IDLE
            },
            onStateChange = { isListening ->
                if (isListening) {
                    // Stop any ongoing AI speech when user starts talking
                    elevenLabsService.stop()
                    _faceState.value = AIFaceState.LISTENING
                } else if (_faceState.value == AIFaceState.LISTENING) {
                    _faceState.value = AIFaceState.IDLE
                }
            }
        )
    }

    private fun processVoiceInput(text: String) {
        viewModelScope.launch {
            _faceState.value = AIFaceState.THINKING
            _errorText.value = ""
            
            // Refresh settings before using services
            geminiService = GeminiService(
                settingsRepository.getGeminiApiKey()?.takeIf { it.isNotBlank() } ?: BuildConfig.GEMINI_API_KEY
            )
            elevenLabsService = ElevenLabsService(
                getApplication(), 
                settingsRepository.getElevenLabsApiKey()?.takeIf { it.isNotBlank() } ?: BuildConfig.ELEVENLABS_API_KEY
            )
            elevenLabsService.voiceId = settingsRepository.getVoiceId()
            
            val response = geminiService.generateResponse(text)
            _aiResponseText.value = response
            
            // Milestone 6/7: Speak the response
            _faceState.value = AIFaceState.SPEAKING
            
            elevenLabsService.speak(response) {
                // Callback when audio finishes playing
                if (_faceState.value == AIFaceState.SPEAKING) {
                    _faceState.value = AIFaceState.IDLE
                }
            }
        }
    }

    fun startListening() {
        _errorText.value = ""
        _spokenText.value = ""
        _aiResponseText.value = ""
        elevenLabsService.stop()
        voiceRecognizer?.startListening()
    }

    fun stopListening() {
        voiceRecognizer?.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecognizer?.destroy()
        elevenLabsService.stop()
    }
}
