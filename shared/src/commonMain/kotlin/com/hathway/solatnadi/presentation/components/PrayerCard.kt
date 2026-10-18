package com.hathway.solatnadi.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme

@Composable
fun PrayerCard(
    prayerTime: PrayerTime,
    onToggleNotification: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNext = prayerTime.isNext

    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes
    val customColors = SolatNadiTheme.customColors
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val cardBackground = if (isNext) colorScheme.primaryContainer else colorScheme.surface
    val cardShadowElevation = if (isNext) dimens.CardElevation else dimens.CardElevation * 2
    val primaryTextColor = if (isNext) colorScheme.onPrimaryContainer else colorScheme.onSurface
    val textSecondaryColor = colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.PrayerCardMinHeight)
            .padding(vertical = spacing.XSmall)
            .shadow(
                elevation = cardShadowElevation,
                shape = shapes.Medium,
                spotColor = colorScheme.onBackground.copy(alpha = 0.1f)
            ),
        shape = shapes.Medium,
        color = cardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimens.PrayerCardHorizontalPadding,
                    vertical = dimens.PrayerCardVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PrayerIcon(
                    prayerName = prayerTime.prayerName,
                    size = dimens.IconLarge + 8.dp
                )

                Spacer(modifier = Modifier.width(spacing.Large))

                Column {
                    Text(
                        text = prayerTime.prayerName.displayName,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = prayerTime.timeString,
                        style = typography.bodyMedium,
                        color = textSecondaryColor
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isNext) {
                    Box(
                        modifier = Modifier
                            .clip(shapes.Small)
                            .background(customColors.success.copy(alpha = 0.15f))
                            .padding(
                                horizontal = dimens.BadgeHorizontalPadding,
                                vertical = dimens.BadgeVerticalPadding
                            )
                    ) {
                        Text(
                            text = "Next Prayer",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = customColors.success
                        )
                    }

                    Spacer(modifier = Modifier.width(spacing.Small))
                }

                Switch(
                    checked = prayerTime.isNotificationEnabled,
                    onCheckedChange = onToggleNotification,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.surface,
                        checkedTrackColor = customColors.success,
                        uncheckedThumbColor = colorScheme.surfaceVariant,
                        uncheckedTrackColor = colorScheme.outline.copy(alpha = 0.4f),
                        uncheckedBorderColor = Color.Transparent,
                        checkedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(spacing.Small))

                BellIcon(
                    isActive = prayerTime.isNotificationEnabled,
                    modifier = Modifier.size(dimens.IconMedium)
                )
            }
        }
    }
}

@Composable
fun BellIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = SolatNadiTheme.customColors
    val colorScheme = MaterialTheme.colorScheme

    Canvas(modifier = modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val color = if (isActive) customColors.success else colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

        val bellPath = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            cubicTo(w * 0.35f, h * 0.15f, w * 0.28f, h * 0.35f, w * 0.28f, h * 0.55f)
            lineTo(w * 0.18f, h * 0.70f)
            lineTo(w * 0.82f, h * 0.70f)
            lineTo(w * 0.72f, h * 0.55f)
            cubicTo(w * 0.72f, h * 0.35f, w * 0.65f, h * 0.15f, w * 0.5f, h * 0.15f)
            close()
        }

        if (isActive) {
            drawPath(path = bellPath, color = color)
            drawCircle(color = color, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.82f))
        } else {
            drawPath(path = bellPath, color = color, style = Stroke(width = w * 0.08f))
            drawCircle(color = color, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.82f), style = Stroke(width = w * 0.08f))
        }
    }
}
