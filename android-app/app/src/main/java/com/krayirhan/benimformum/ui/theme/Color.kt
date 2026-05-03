package com.krayirhan.benimformum.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Ürün renk tablosu (arka plan, yüzey, birincil, su, tipografi, sınır).
 * Material [lightColorScheme]/[darkColorScheme] ve [AppColorScheme] buradan beslenir.
 */

// —— Material ana eksen (yeşim birincil + kum + adaçayı) ——
val SteelPrimary = Color(0xFF3D7A5E)
val SteelOnPrimary = Color(0xFFFFFFFF)
val SteelPrimaryContainer = Color(0xFFDDEFE6)
val SteelOnPrimaryContainer = Color(0xFF1F2A24)

val SandSecondary = Color(0xFFD4A574)
val SandOnSecondary = Color(0xFF1A140E)
val SandSecondaryContainer = Color(0xFFFFE8D4)
val SandOnSecondaryContainer = Color(0xFF4A3418)

val SageTertiary = Color(0xFF5C8F7B)
val SageOnTertiary = Color(0xFFFFFFFF)
val SageTertiaryContainer = Color(0xFFD4EBE2)
val SageOnTertiaryContainer = Color(0xFF153326)

/** Su metriği ve ilerleme çizgileri için mavi-yeşil vurgu. */
val SkyAccent = Color(0xFF3A8FB7)
val SkyAccentContainer = Color(0xFFD4EBF4)
val OnSkyAccentContainer = Color(0xFF0C3040)

// —— Açık tema yüzeyleri ——
val LightBackground = Color(0xFFF7F9F8)
val LightOnBackground = Color(0xFF1F2A24)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1F2A24)
val LightSurfaceVariant = Color(0xFFF2F7F4)
val LightOnSurfaceVariant = Color(0xFF5F6B64)
val LightOutline = Color(0xFFD8E6DE)
val LightOutlineVariant = Color(0xFFE8F1EC)

/** Günün ritmi kartı ve yumuşak yüzeyler. */
val FormHeroCardFill = Color(0xFFE8F5EF)
val FormHeroCardBorder = Color(0xFFD8E6DE)
val FormHeroCardFillDark = Color(0xFF23352C)
val FormHeroCardBorderDark = Color(0xFF2E4A3A)

// —— Koyu tema (marka yeşili + sakin yüzeyler) ——
val DarkPrimary = Color(0xFF5AAD85)
val DarkOnPrimary = Color(0xFF0F1612)
val DarkPrimaryContainer = Color(0xFF2E4A3A)
val DarkOnPrimaryContainer = Color(0xFFEAF2EA)

val DarkSecondary = Color(0xFFEDD9A8)
val DarkOnSecondary = Color(0xFF1E1305)
val DarkSecondaryContainer = Color(0xFF533E20)
val DarkOnSecondaryContainer = Color(0xFFFDE4CA)

val DarkTertiary = Color(0xFF96D3C1)
val DarkOnTertiary = Color(0xFF051D17)
val DarkTertiaryContainer = Color(0xFF1D4D40)
val DarkOnTertiaryContainer = Color(0xFFD0F0E5)

val DarkBackground = Color(0xFF111F19)
val DarkOnBackground = Color(0xFFEAF2EA)
val DarkSurface = Color(0xFF1A2E25)
val DarkOnSurface = Color(0xFFEAF2EA)
val DarkSurfaceVariant = Color(0xFF243830)
val DarkOnSurfaceVariant = Color(0xFFA8D8C0)
val DarkOutline = Color(0xFF2E4A3A)
val DarkOutlineVariant = Color(0xFF2E4A3A)

val ErrorRed = Color(0xFFFFB4AB)
val OnErrorRed = Color(0xFF690005)
val LightErrorRed = Color(0xFFB3261E)
val LightOnErrorRed = Color(0xFFFFFFFF)

val ScoreLow = Color(0xFFD4897A)
val ScoreMid = Color(0xFFC9A06A)
val ScoreHigh = Color(0xFF5A9E82)

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
    val nightSnack: Color,
    val nightSnackContainer: Color,
    val onNightSnackContainer: Color,
    val privacy: Color,
    val onPrivacy: Color,
    val privacyContainer: Color,
    val onPrivacyContainer: Color,
    val progressAccent: Color,
    val scoreLow: Color,
    val scoreMid: Color,
    val scoreHigh: Color,
    val insightCardFill: Color,
    val insightCardBorder: Color,
    val heroScoreTrack: Color
)

val MetricSleep = Color(0xFF786BC4)
val MetricSleepContainer = Color(0xFFEDE9F7)
val OnMetricSleepContainer = Color(0xFF251A37)

val MetricEnergy = Color(0xFFD6A13D)
val MetricEnergyContainer = Color(0xFFFDF0D8)
val OnMetricEnergyContainer = Color(0xFF3D2A08)

val MetricMood = Color(0xFFC46F5B)
val MetricMoodContainer = Color(0xFFFBE8E4)
val OnMetricMoodContainer = Color(0xFF3D1F18)

val MetricWeight = Color(0xFF8B7E74)
val MetricWeightContainer = Color(0xFFF2EEEB)
val OnMetricWeightContainer = Color(0xFF2A2522)

val MetricNightSnack = Color(0xFF8FAE9A)
val MetricNightSnackContainer = Color(0xFFE8F2EC)
val OnMetricNightSnackContainer = Color(0xFF1F2A24)

val LightAppColorScheme = AppColorScheme(
    water = SkyAccent,
    waterContainer = SkyAccentContainer,
    onWaterContainer = OnSkyAccentContainer,
    sleep = MetricSleep,
    sleepContainer = MetricSleepContainer,
    onSleepContainer = OnMetricSleepContainer,
    energy = MetricEnergy,
    energyContainer = MetricEnergyContainer,
    onEnergyContainer = OnMetricEnergyContainer,
    mood = MetricMood,
    moodContainer = MetricMoodContainer,
    onMoodContainer = OnMetricMoodContainer,
    weight = MetricWeight,
    weightContainer = MetricWeightContainer,
    onWeightContainer = OnMetricWeightContainer,
    nightSnack = MetricNightSnack,
    nightSnackContainer = MetricNightSnackContainer,
    onNightSnackContainer = OnMetricNightSnackContainer,
    privacy = SteelPrimary,
    onPrivacy = SteelOnPrimary,
    privacyContainer = SteelPrimaryContainer,
    onPrivacyContainer = SteelOnPrimaryContainer,
    progressAccent = SteelPrimary,
    scoreLow = Color(0xFFA84949),
    scoreMid = Color(0xFFC9975F),
    scoreHigh = Color(0xFF4B7E6A),
    insightCardFill = LightSurfaceVariant,
    insightCardBorder = LightOutline,
    heroScoreTrack = Color(0xFFD8E6DE)
)

val DarkAppColorScheme = AppColorScheme(
    water = Color(0xFF7EC4E8),
    waterContainer = Color(0xFF124456),
    onWaterContainer = Color(0xFFD4EBF4),
    sleep = Color(0xFFB4A8E8),
    sleepContainer = Color(0xFF3A3258),
    onSleepContainer = Color(0xFFEDE9F7),
    energy = Color(0xFFE8C46A),
    energyContainer = Color(0xFF5C4518),
    onEnergyContainer = Color(0xFFFDF0D8),
    mood = Color(0xFFE89A8A),
    moodContainer = Color(0xFF5C3228),
    onMoodContainer = Color(0xFFFBE8E4),
    weight = Color(0xFFC4B8B0),
    weightContainer = Color(0xFF3A342F),
    onWeightContainer = Color(0xFFF2EEEB),
    nightSnack = Color(0xFFA8C9B6),
    nightSnackContainer = Color(0xFF2A4034),
    onNightSnackContainer = Color(0xFFE8F2EC),
    privacy = DarkPrimary,
    onPrivacy = DarkOnPrimary,
    privacyContainer = DarkPrimaryContainer,
    onPrivacyContainer = DarkOnPrimaryContainer,
    progressAccent = DarkPrimary,
    scoreLow = Color(0xFFFF9F96),
    scoreMid = Color(0xFFEDD9A8),
    scoreHigh = Color(0xFF96D3C1),
    insightCardFill = DarkSurfaceVariant,
    insightCardBorder = DarkOutline,
    heroScoreTrack = Color(0xFF2E4A3A)
)
