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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

@Composable
fun PrayerCard(
    prayerTime: PrayerTime,
    onToggleNotification: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNext = prayerTime.isNext
    val cardBackground = if (isNext) Color(0xFFEAF8EE) else Color.White
    val cardShadowElevation = if (isNext) 2.dp else 4.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .shadow(
                elevation = cardShadowElevation,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x1A000000)
            ),
        shape = RoundedCornerShape(20.dp),
        color = cardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PrayerIcon(
                    prayerName = prayerTime.prayerName,
                    size = 36.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = prayerTime.prayerName.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolatNadiPalette.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = prayerTime.timeString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF788590)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isNext) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFD3EEDC))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Next Prayer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B6338)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Switch(
                    checked = prayerTime.isNotificationEnabled,
                    onCheckedChange = onToggleNotification,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF1BB762),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD8DCE0),
                        uncheckedBorderColor = Color.Transparent,
                        checkedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                BellIcon(isActive = prayerTime.isNotificationEnabled)
            }
        }
    }
}

@Composable
fun BellIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val color = if (isActive) Color(0xFF1BB762) else Color(0xFF9EA7B0)

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
