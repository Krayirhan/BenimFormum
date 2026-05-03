package com.krayirhan.benimformum

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.navigation.AppNavHost
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (state.themePreference) {
                ThemePreference.SYSTEM -> systemDark
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }
            val view = LocalView.current
            SideEffect {
                val window = (view.context as? Activity)?.window ?: return@SideEffect
                val insets = WindowCompat.getInsetsController(window, view)
                insets.isAppearanceLightStatusBars = !darkTheme
                insets.isAppearanceLightNavigationBars = !darkTheme
            }
            BenimFormumTheme(
                themePreference = state.themePreference,
                dynamicColor = state.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}
