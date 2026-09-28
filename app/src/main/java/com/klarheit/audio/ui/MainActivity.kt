package com.klarheit.audio.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.klarheit.audio.service.KlarheitService
import com.klarheit.audio.ui.navigation.KlarheitNavigation
import com.klarheit.audio.ui.theme.KlarheitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KlarheitService.startService(this)
        enableEdgeToEdge()
        setContent {
            KlarheitTheme {
                KlarheitNavigation()
            }
        }
    }
}
