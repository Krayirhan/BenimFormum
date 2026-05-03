package com.krayirhan.benimformum.navigation

import android.animation.ValueAnimator
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krayirhan.benimformum.feature.history.HistoryScreen
import com.krayirhan.benimformum.feature.onboarding.OnboardingScreen
import com.krayirhan.benimformum.feature.report.WeeklySummaryScreen
import com.krayirhan.benimformum.feature.settings.SettingsScreen
import com.krayirhan.benimformum.feature.today.TodayScreen
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun AppNavHost(
    viewModel: AppNavViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? ComponentActivity

    when {
        state.isLoading -> AppLoadingScreen()
        !state.onboardingCompleted -> OnboardingScreen(
            onCompleted = viewModel::completeOnboarding,
            onExitFromFirstStepBack = { activity?.finish() }
        )
        else -> MainTabsNavHost()
    }
}

@Composable
private fun AppLoadingScreen() {
    val appColors = MaterialTheme.appColors
    val track = appColors.heroScoreTrack
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Benim Formum",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Uygulama hazırlanıyor...",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            modifier = Modifier.padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = appColors.privacy,
                    trackColor = appColors.heroScoreTrack
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(14.dp),
                        shape = MaterialTheme.shapes.small,
                        color = track
                    ) {}
                    Surface(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.92f)
                            .height(12.dp),
                        shape = MaterialTheme.shapes.small,
                        color = track
                    ) {}
                }
            }
        }
    }
}

private data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTabsNavHost() {
    val navController = rememberNavController()
    val reduceMotion = !ValueAnimator.areAnimatorsEnabled()
    val useRail = LocalConfiguration.current.screenWidthDp >= 600
    val tabs = listOf(
        BottomTab(
            route = AppDestinations.TODAY,
            label = "Bugün",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomTab(
            route = AppDestinations.HISTORY,
            label = "Geçmiş",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        ),
        BottomTab(
            route = AppDestinations.WEEKLY_SUMMARY,
            label = "Haftalık",
            selectedIcon = Icons.Filled.Insights,
            unselectedIcon = Icons.Outlined.Insights
        ),
        BottomTab(
            route = AppDestinations.SETTINGS,
            label = "Ayarlar",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTitle = tabs.firstOrNull { it.route == currentRoute }?.label ?: "Bugün"
    val appColors = MaterialTheme.appColors

    fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToToday: () -> Unit = { navigate(AppDestinations.TODAY) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        currentTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (!useRail) {
                Column {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 0.dp
                    ) {
                        tabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigate(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = appColors.privacy,
                                    selectedTextColor = appColors.privacy,
                                    indicatorColor = appColors.privacyContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (useRail) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigate(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = appColors.privacy,
                                selectedTextColor = appColors.privacy,
                                indicatorColor = appColors.privacyContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.TODAY,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        if (reduceMotion) {
                            fadeIn(animationSpec = tween(0))
                        } else {
                            slideInHorizontally(
                                animationSpec = spring(dampingRatio = 0.88f, stiffness = 400f),
                                initialOffsetX = { it / 10 }
                            ) + fadeIn(animationSpec = tween(240))
                        }
                    },
                    exitTransition = { fadeOut(animationSpec = tween(if (reduceMotion) 0 else 140)) },
                    popEnterTransition = {
                        if (reduceMotion) {
                            fadeIn(animationSpec = tween(0))
                        } else {
                            slideInHorizontally(
                                animationSpec = spring(dampingRatio = 0.88f, stiffness = 400f),
                                initialOffsetX = { -it / 10 }
                            ) + fadeIn(animationSpec = tween(240))
                        }
                    },
                    popExitTransition = { fadeOut(animationSpec = tween(if (reduceMotion) 0 else 140)) }
                ) {
                    composable(AppDestinations.TODAY) {
                        TodayScreen(contentPadding = PaddingValues())
                    }
                    composable(AppDestinations.HISTORY) {
                        HistoryScreen(
                            contentPadding = PaddingValues(),
                            onNavigateToToday = navigateToToday
                        )
                    }
                    composable(AppDestinations.WEEKLY_SUMMARY) {
                        WeeklySummaryScreen(
                            contentPadding = PaddingValues(),
                            onNavigateToToday = navigateToToday
                        )
                    }
                    composable(AppDestinations.SETTINGS) {
                        SettingsScreen(contentPadding = PaddingValues())
                    }
                }
            }
        }
    }
}
