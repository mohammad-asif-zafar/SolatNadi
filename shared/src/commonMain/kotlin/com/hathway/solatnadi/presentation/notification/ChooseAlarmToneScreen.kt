package com.hathway.solatnadi.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.AdhanTone
import com.hathway.solatnadi.domain.model.AdhanTones
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.ToneCategory
import com.hathway.solatnadi.presentation.settings.SettingsSectionHeader
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode

@Composable
fun ChooseAlarmToneScreen(
    viewModel: ChooseAlarmToneViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ChooseAlarmToneScreenContent(
        uiState = uiState,
        onSelectCategory = viewModel::selectCategory,
        onSelectTone = viewModel::selectTone,
        onTogglePreview = viewModel::toggleAudioPreview,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ChooseAlarmToneScreenContent(
    uiState: ChooseAlarmToneUiState,
    onSelectCategory: (ToneCategory) -> Unit,
    onSelectTone: (AdhanTone) -> Unit,
    onTogglePreview: (AdhanTone) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val filteredTones = AdhanTones.allTones.filter { it.category == uiState.selectedCategory }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = dimens.ScreenHorizontalPadding,
                vertical = dimens.ScreenVerticalPadding
            )
    ) {
        // Navigation Header block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(vertical = spacing.Small, horizontal = spacing.XSmall)
            )

            Spacer(modifier = Modifier.width(spacing.Medium))

            Column {
                Text(
                    text = "Choose alarm tone",
                    style = typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Select a sound for ${uiState.prayerName.displayName} notification",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Category Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surfaceVariant, shape = shapes.Pill)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToneCategory.entries.forEach { category ->
                val isSelected = uiState.selectedCategory == category
                val label = when (category) {
                    ToneCategory.ADHAN -> "Adhan"
                    ToneCategory.ALARMS -> "Alarms"
                    ToneCategory.DEVICE -> "Device"
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shapes.Pill)
                        .clickable { onSelectCategory(category) },
                    shape = shapes.Pill,
                    color = if (isSelected) colorScheme.surface else colorScheme.surfaceVariant,
                    tonalElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            style = typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Section Title
        val sectionTitle = when (uiState.selectedCategory) {
            ToneCategory.ADHAN -> "Adhan sounds"
            ToneCategory.ALARMS -> "System alarm sounds"
            ToneCategory.DEVICE -> "Device & silent modes"
        }

        SettingsSectionHeader(title = sectionTitle, icon = "🎵")

        // Tones List Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Small)) {
                filteredTones.forEachIndexed { index, tone ->
                    val isSelected = uiState.selectedSoundKey == tone.key
                    val isPlaying = uiState.currentlyPlayingKey == tone.key

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTone(tone) }
                            .padding(horizontal = spacing.Small, vertical = spacing.Medium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (tone.key == "silent") {
                                Surface(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                    shape = CircleShape,
                                    color = colorScheme.surfaceVariant
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "🔕",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { onTogglePreview(tone) },
                                    shape = CircleShape,
                                    color = if (isPlaying) colorScheme.primary else colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isPlaying) "⏸" else "▶",
                                            fontSize = 14.sp,
                                            color = if (isPlaying) colorScheme.onPrimary else colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(spacing.Medium))

                            Column {
                                Text(
                                    text = tone.title,
                                    style = typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colorScheme.primary else colorScheme.onSurface
                                )
                                Text(
                                    text = tone.subtitle,
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectTone(tone) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorScheme.primary
                            )
                        )
                    }

                    if (index < filteredTones.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = spacing.XSmall),
                            color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.XXLarge))
    }
}

@Preview(name = "Choose Alarm Tone Light Mode", showBackground = true)
@Composable
fun ChooseAlarmToneScreenLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        ChooseAlarmToneScreenContent(
            uiState = ChooseAlarmToneUiState(
                prayerName = PrayerName.FAJR,
                selectedCategory = ToneCategory.ADHAN,
                selectedSoundKey = "default_adhan"
            ),
            onSelectCategory = {},
            onSelectTone = {},
            onTogglePreview = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Choose Alarm Tone Dark Mode", showBackground = true)
@Composable
fun ChooseAlarmToneScreenDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        ChooseAlarmToneScreenContent(
            uiState = ChooseAlarmToneUiState(
                prayerName = PrayerName.FAJR,
                selectedCategory = ToneCategory.ADHAN,
                selectedSoundKey = "default_adhan"
            ),
            onSelectCategory = {},
            onSelectTone = {},
            onTogglePreview = {},
            onNavigateBack = {}
        )
    }
}
