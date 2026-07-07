package com.example.hiatoo.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hiatoo.animation.AIFace
import com.example.hiatoo.models.AIFaceState
import com.example.hiatoo.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val faceState by viewModel.faceState.collectAsState()
    val spokenText by viewModel.spokenText.collectAsState()
    val errorText by viewModel.errorText.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startListening()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // Settings Button
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextPrimary
            )
        }

        // AI Face
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(400.dp, 300.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(DarkGlass)
                .border(1.dp, GlassBorder, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            AIFace(state = faceState)
        }

        // Temporary display for Speech to Text result/errors and AI response
        val aiResponseText by viewModel.aiResponseText.collectAsState()

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = spokenText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "You: $spokenText",
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            AnimatedVisibility(
                visible = aiResponseText.isNotEmpty() || errorText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (aiResponseText.isNotEmpty()) {
                    Text(
                        text = "HIATOO: $aiResponseText",
                        color = AccentCyan,
                        textAlign = TextAlign.Center
                    )
                } else if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = AccentPurple,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Text
            Text(
                text = faceState.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Microphone Button with Glassmorphism
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentCyan.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    .border(1.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (faceState == AIFaceState.LISTENING) {
                            viewModel.stopListening()
                        } else {
                            if (hasPermission) {
                                viewModel.startListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DarkGlass)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = AccentCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
