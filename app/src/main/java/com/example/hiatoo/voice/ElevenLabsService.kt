package com.example.hiatoo.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ElevenLabsService(
    private val context: Context,
    private val apiKey: String
) {
    private val client = OkHttpClient()
    private var mediaPlayer: MediaPlayer? = null
    
    // Default Voice ID (can be configured via Settings later)
    var voiceId: String = "EXAVITQu4vr4xnSDxMaL" // Example: "Rachel" or any other ID

    suspend fun speak(text: String, onPlaybackComplete: () -> Unit) = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "YOUR_ELEVENLABS_API_KEY_HERE") {
            Log.e("ElevenLabsService", "API Key is missing!")
            withContext(Dispatchers.Main) { onPlaybackComplete() }
            return@withContext
        }

        val json = JSONObject().apply {
            put("text", text)
            put("model_id", "eleven_monolingual_v1")
            put("voice_settings", JSONObject().apply {
                put("stability", 0.5)
                put("similarity_boost", 0.7)
            })
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        
        val request = Request.Builder()
            .url("https://api.elevenlabs.io/v1/text-to-speech/$voiceId?output_format=mp3_44100_128")
            .post(requestBody)
            .addHeader("xi-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bytes = response.body?.bytes()
                if (bytes != null) {
                    playAudioFromBytes(bytes, onPlaybackComplete)
                } else {
                    withContext(Dispatchers.Main) { onPlaybackComplete() }
                }
            } else {
                Log.e("ElevenLabsService", "Error: ${response.code} ${response.message}")
                withContext(Dispatchers.Main) { onPlaybackComplete() }
            }
        } catch (e: IOException) {
            Log.e("ElevenLabsService", "Network Error", e)
            withContext(Dispatchers.Main) { onPlaybackComplete() }
        }
    }

    private suspend fun playAudioFromBytes(audioData: ByteArray, onPlaybackComplete: () -> Unit) {
        withContext(Dispatchers.Main) {
            try {
                // Save to temporary file
                val tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
                tempFile.deleteOnExit()
                
                FileOutputStream(tempFile).use { fos ->
                    fos.write(audioData)
                }

                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(tempFile.absolutePath)
                    setOnCompletionListener {
                        it.release()
                        mediaPlayer = null
                        onPlaybackComplete()
                    }
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                Log.e("ElevenLabsService", "Playback Error", e)
                onPlaybackComplete()
            }
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}
