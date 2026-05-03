package com.krayirhan.benimformum.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val MintPrimary = Color(0xFF2F7D69)
val MintOnPrimary = Color(0xFFFFFFFF)
val MintPrimaryContainer = Color(0xFFD5F3E8)
val MintOnPrimaryContainer = Color(0xFF073729)

val AmberSecondary = Color(0xFF94612A)
val AmberOnSecondary = Color(0xFFFFFFFF)
val AmberSecondaryContainer = Color(0xFFFFDDB8)
val AmberOnSecondaryContainer = Color(0xFF331F05)

val RoseTertiary = Color(0xFFA65A78)
val RoseOnTertiary = Color(0xFFFFFFFF)
val RoseTertiaryContainer = Color(0xFFFFD9E5)
val RoseOnTertiaryContainer = Color(0xFF3E1F2C)

val SkyAccent = Color(0xFF2F7EA7)
val SkyAccentContainer = Color(0xFFC9E5F4)
val OnSkyAccentContainer = Color(0xFF082F42)

val LightBackground = Color(0xFFF7FAF7)
val LightOnBackground = Color(0xFF17211D)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF17211D)
val LightSurfaceVariant = Color(0xFFE7EFEA)
val LightOnSurfaceVariant = Color(0xFF52615B)
val LightOutline = Color(0xFFB7C7C0)
val LightOutlineVariant = Color(0xFFDCE8E2)

val DarkPrimary = Color(0xFF7BD7BB)
val DarkOnPrimary = Color(0xFF00382A)
val DarkPrimaryContainer = Color(0xFF1A4B3E)
val DarkOnPrimaryContainer = Color(0xFFC2F2E0)
val DarkSecondary = Color(0xFFFFC98F)
val DarkOnSecondary = Color(0xFF4A2800)
val DarkSecondaryContainer = Color(0xFF5D3710)
val DarkOnSecondaryContainer = Color(0xFFFFE0BD)
val DarkTertiary = Color(0xFFF4B6CC)
val DarkOnTertiary = Color(0xFF4A2032)
val DarkTertiaryContainer = Color(0xFF5C2E40)
val DarkOnTertiaryContainer = Color(0xFFFFD9E5)

val DarkBackground = Color(0xFF101714)
val DarkOnBackground = Color(0xFFE3ECE7)
val DarkSurface = Color(0xFF18211D)
val DarkOnSurface = Color(0xFFE3ECE7)
val DarkSurfaceVariant = Color(0xFF24302B)
val DarkOnSurfaceVariant = Color(0xFFB9C8C1)
val DarkOutline = Color(0xFF3B4A44)
val DarkOutlineVariant = Color(0x29FFFFFF)

val ErrorRed = Color(0xFFFFB4AB)
val OnErrorRed = Color(0xFF690005)
val LightErrorRed = Color(0xFFBA1A1A)
val LightOnErrorRed = Color(0xFFFFFFFF)

val ScoreLow = Color(0xFFF77D7D)
val ScoreMid = Color(0xFFFFB877)
val ScoreHigh = Color(0xFF4CAF8F)

@Immutable
data class AppColorScheme(
    val water: Color,
    val waterContainer: Color,
    val onWaterContainer: Color,
    val sleep: Color,
    val sleepContainer: Color,
    val onSleepContainer: Color,
    val energy: Color,
    val energyContainer: Color,
    val onEnergyContainer: Color,
    val mood: Color,
    val moodContainer: Color,
    val onMoodContainer: Color,
    val weight: Color,
    val weightContainer: Color,
    val onWeightContainer: Color,
    /** Gece atıştırması; enerji paletinden ayrı sıcak “bisküvi” tonu. */
    val nightSnack: Color,
    val nightSnackContainer: Color,
    val onNightSnackContainer: Color,
    val privacy: Color,
    val privacyContainer: Color,
    val onPrivacyContainer: Color,
    /** Haftalık özet hero halkası / sayı; gizlilik semantiğinden ayrı (Sprint 4 — S4-1). */
    val progressAccent: Color,
    val scoreLow: Color,
    val scoreMid: Color,
    val scoreHigh: Color
)

val LightAppColorScheme = AppColorScheme(
    water = Color(0xFF2F7EA7),
    waterContainer = Color(0xFFC9E5F4),
    onWaterContainer = Color(0xFF082F42),
    sleep = Color(0xFF5967A8),
    sleepContainer = Color(0xFFE0E5FF),
    onSleepContainer = Color(0xFF172153),
    energy = Color(0xFF94612A),
    energyContainer = Color(0xFFFFDDB8),
    onEnergyContainer = Color(0xFF331F05),
    mood = Color(0xFFA65A78),
    moodContainer = Color(0xFFFFD9E5),
    onMoodContainer = Color(0xFF3E1F2C),
    weight = Color(0xFF63706A),
    weightContainer = Color(0xFFE0E8E3),
    onWeightContainer = Color(0xFF1E2A25),
    nightSnack = Color(0xFF8B6B4A),
    nightSnackContainer = Color(0xFFF5EBE3),
    onNightSnackContainer = Color(0xFF2A1E14),
    privacy = Color(0xFF2F7D69),
    privacyContainer = Color(0xFFD5F3E8),
    onPrivacyContainer = Color(0xFF073729),
    progressAccent = SkyAccent,
    scoreLow = Color(0xFFC84747),
    scoreMid = Color(0xFF94612A),
    scoreHigh = Color(0xFF2F7D69)
)

val DarkAppColorScheme = AppColorScheme(
    water = Color(0xFF8BC9E8),
    waterContainer = Color(0xFF143345),
    onWaterContainer = Color(0xFFC9E5F4),
    sleep = Color(0xFFAEBBFF),
    sleepContainer = Color(0xFF29335F),
    onSleepContainer = Color(0xFFE1E6FF),
    energy = Color(0xFFFFC98F),
    energyContainer = Color(0xFF5D3710),
    onEnergyContainer = Color(0xFFFFE0BD),
    mood = Color(0xFFF4B6CC),
    moodContainer = Color(0xFF5C2E40),
    onMoodContainer = Color(0xFFFFD9E5),
    weight = Color(0xFFB7C6BF),
    weightContainer = Color(0xFF2D3934),
    onWeightContainer = Color(0xFFE0E8E3),
    nightSnack = Color(0xFFD4B8A0),
    nightSnackContainer = Color(0xFF403328),
    onNightSnackContainer = Color(0xFFF5EBE3),
    privacy = Color(0xFF7BD7BB),
    privacyContainer = Color(0xFF1A4B3E),
    onPrivacyContainer = Color(0xFFC2F2E0),
    progressAccent = Color(0xFF5DADE2),
    scoreLow = Color(0xFFFF8D8D),
    scoreMid = Color(0xFFFFC98F),
    scoreHigh = Color(0xFF7BD7BB)
)
