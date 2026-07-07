package com.example.hiatoo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hiatoo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val elevenLabsApiKey by viewModel.elevenLabsApiKey.collectAsState()
    val voiceId by viewModel.voiceId.collectAsState()
    val speechSpeed by viewModel.speechSpeed.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Settings Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCard {
                OutlinedTextField(
                    value = geminiApiKey,
                    onValueChange = { viewModel.updateGeminiApiKey(it) },
                    label = { Text("Gemini API Key", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = elevenLabsApiKey,
                    onValueChange = { viewModel.updateElevenLabsApiKey(it) },
                    label = { Text("ElevenLabs API Key", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = voiceId,
                    onValueChange = { viewModel.updateVoiceId(it) },
                    label = { Text("ElevenLabs Voice ID", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsCard {
                Text("Speech Speed: ${"%.1f".format(speechSpeed)}x", color = TextPrimary)
                Slider(
                    value = speechSpeed,
                    onValueChange = { viewModel.updateSpeechSpeed(it) },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentCyan,
                        activeTrackColor = AccentCyan,
                        inactiveTrackColor = GlassBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", color = TextPrimary)
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { viewModel.updateDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentCyan,
                            checkedTrackColor = DarkGlass
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsCard {
                Text("About HIATOO", style = MaterialTheme.typography.bodyLarge, color = AccentCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Version 1.0 MVP\nVoice-first Android AI Assistant.", color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGlass)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column {
            content()
        }
    }
}
