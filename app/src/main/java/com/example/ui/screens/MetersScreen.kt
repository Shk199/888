package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.MeterDefinition
import com.example.engine.MeterDetector
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy900
import com.example.ui.theme.Royal100
import com.example.ui.theme.Royal50
import com.example.ui.theme.Royal800
import com.example.ui.theme.Royal900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.TajawalFontFamily

@Composable
fun MetersScreen(
    onTryMeter: (String) -> Unit
) {
    val meters = MeterDetector.METERS

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("tab_meters_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // رأس التبويب
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "بحور الشعر العربي الستة عشر",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Navy900,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = "مفاتيح الخليل الفراهيدي وأوزانها وأبيات الشاهد",
                        fontFamily = TajawalFontFamily,
                        fontSize = 12.sp,
                        color = Slate500,
                        letterSpacing = 0.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Royal50)
                        .border(1.dp, Royal100, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "١٦ بحراً",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Royal800,
                        letterSpacing = 0.sp
                    )
                }
            }
        }

        // قائمة البحور
        items(meters) { meter ->
            MeterCard(meter = meter, onTryMeter = onTryMeter)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MeterCard(
    meter: MeterDefinition,
    onTryMeter: (String) -> Unit
) {
    val typeText = if (meter.forms.size > 1) "تام ومجزوء" else meter.forms.firstOrNull()?.type ?: "تام"
    val basesText = meter.forms.firstOrNull()?.bases?.joinToString(" ") ?: ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meter_card_${meter.id}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // العنوان والنوع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بحر ${meter.name}",
                    fontFamily = TajawalFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Navy900,
                    letterSpacing = 0.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gold100)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = typeText,
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Gold600,
                        letterSpacing = 0.sp
                    )
                }
            }

            // الوصف
            if (meter.description.isNotEmpty()) {
                Text(
                    text = meter.description,
                    fontFamily = TajawalFontFamily,
                    fontSize = 12.sp,
                    color = Slate600,
                    lineHeight = 18.sp,
                    letterSpacing = 0.sp
                )
            }

            // مفتاح البحر والتفعيلات
            if (meter.keyVerse.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Royal100)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "مفتاح البحر والتفعيلات:",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Gold600,
                            letterSpacing = 0.sp
                        )
                        Text(
                            text = meter.keyVerse,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Navy900,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            letterSpacing = 0.sp
                        )
                        if (basesText.isNotEmpty()) {
                            Text(
                                text = basesText,
                                fontFamily = TajawalFontFamily,
                                fontSize = 12.sp,
                                color = Slate500,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }

            // بيت الشاهد
            if (meter.exampleVerse.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Royal50.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Royal100)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "بيت الشاهد:",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Royal900,
                                letterSpacing = 0.sp
                            )
                            if (meter.examplePoet.isNotEmpty()) {
                                Text(
                                    text = meter.examplePoet,
                                    fontFamily = TajawalFontFamily,
                                    fontSize = 11.sp,
                                    color = Slate500,
                                    letterSpacing = 0.sp
                                )
                            }
                        }
                        Text(
                            text = meter.exampleVerse,
                            fontFamily = AmiriFontFamily,
                            fontSize = 17.sp,
                            color = Navy900,
                            lineHeight = 24.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }

            // زر التجربة في المحلل
            if (meter.exampleVerse.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onTryMeter(meter.exampleVerse) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold600,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "جرّبه في المحلل",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
