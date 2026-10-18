package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.FootMatch
import com.example.ui.AnalyzerViewModel
import com.example.ui.FullAnalysisData
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold300
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold600
import com.example.ui.theme.Gold700
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red50
import com.example.ui.theme.Red600
import com.example.ui.theme.Royal100
import com.example.ui.theme.Royal50
import com.example.ui.theme.Royal700
import com.example.ui.theme.Royal800
import com.example.ui.theme.Royal900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.TajawalFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyzerScreen(
    viewModel: AnalyzerViewModel,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val inputText by viewModel.inputText.collectAsState()
    val inputError by viewModel.inputError.collectAsState()
    val optMeter by viewModel.optMeter.collectAsState()
    val optDiacritics by viewModel.optDiacritics.collectAsState()
    val optGrammar by viewModel.optGrammar.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisData by viewModel.analysisData.collectAsState()
    val symbolsMode by viewModel.symbolsMode.collectAsState()
    val isEditingTashkeel by viewModel.isEditingTashkeel.collectAsState()

    var editableTashkeelText by remember { mutableStateOf("") }

    val copyToClipboard = { text: String, msg: String ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Verse", text)
        clipboard.setPrimaryClip(clip)
        onShowToast(msg)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ================= بطاقة الإدخال =================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analyzer_input_card"),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // رأس البطاقة
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_mark),
                                contentDescription = "شعار ميزان",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column {
                            Text(
                                text = "الديوان الذكي",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Gold600,
                                letterSpacing = 0.sp
                            )
                            Text(
                                text = "مُحَلِّلُ الشِّعْرِ العَرَبِيّ",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Navy900,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }

                // صف التسمية وعدد الحروف
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Gold600,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "أدخل شطري البيت الشعري أو البيت كاملاً",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Navy900,
                            letterSpacing = 0.sp
                        )
                    }
                    Text(
                        text = "${inputText.length} حرف",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Slate400,
                        letterSpacing = 0.sp
                    )
                }

                // صندوق إدخال النص
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.setInputText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verse_input_field"),
                    placeholder = {
                        Text(
                            text = "عَلى قَدْرِ أَهْلِ العَزْمِ تَأْتِي العَزَائِمُ ... وَتَأْتِي عَلَى قَدْرِ الكِرَامِ المَكَارِمُ",
                            fontFamily = AmiriFontFamily,
                            fontSize = 17.sp,
                            color = Slate400,
                            letterSpacing = 0.sp
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = AmiriFontFamily,
                        fontSize = 19.sp,
                        color = Navy900,
                        letterSpacing = 0.sp,
                        lineHeight = 28.sp
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate50,
                        unfocusedContainerColor = Slate50,
                        focusedBorderColor = Royal700,
                        unfocusedBorderColor = Slate200
                    ),
                    minLines = 3,
                    maxLines = 5,
                    isError = inputError != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                if (inputError != null) {
                    Text(
                        text = inputError!!,
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Red600,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("input_error_text")
                    )
                }

                // التلميح السفلي
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "افصل بين الصدر والعجز بـ (…) أو (/)",
                        fontFamily = TajawalFontFamily,
                        fontSize = 11.sp,
                        color = Slate500,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = "✤ بحور الخليل الفراهيدي ✤",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Royal900,
                        letterSpacing = 0.sp
                    )
                }

                // خيارات التحليل (العروض والبحر، الضبط والتشكيل، الإعراب والنحو)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionCheckboxCard(
                        modifier = Modifier.weight(1f),
                        title = "العروض والبحر",
                        checked = optMeter,
                        onCheckedChange = { viewModel.setOptMeter(it) },
                        tag = "opt_meter"
                    )
                    OptionCheckboxCard(
                        modifier = Modifier.weight(1f),
                        title = "الضبط والتشكيل",
                        checked = optDiacritics,
                        onCheckedChange = { viewModel.setOptDiacritics(it) },
                        tag = "opt_diacritics"
                    )
                    OptionCheckboxCard(
                        modifier = Modifier.weight(1f),
                        title = "الإعراب والنحو",
                        checked = optGrammar,
                        onCheckedChange = { viewModel.setOptGrammar(it) },
                        tag = "opt_grammar"
                    )
                }

                // أزرار التحكم
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // زر التحليل الرئيسي
                    Button(
                        onClick = { viewModel.runAnalysis() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_analyze"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold600,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Gold100,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "تحليل البيت الشعري",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }

                    // زر النموذج
                    OutlinedButton(
                        onClick = { viewModel.insertSample() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_sample"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Navy900
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryEdu,
                                contentDescription = "نموذج",
                                tint = Royal700,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "نموذج",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }

                    // زر المسح
                    OutlinedButton(
                        onClick = { viewModel.clearInput() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_clear"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Red600
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "مسح الحقل",
                            tint = Red600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ================= بطاقة التحميل =================
        AnimatedVisibility(visible = isAnalyzing) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loading_card"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(44.dp),
                        color = Gold600,
                        strokeWidth = 3.5.dp
                    )
                    Text(
                        text = "جاري موازنة البيت وتدقيقه...",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Navy900,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = "نستخرج التفاعيل، ونفحص الزحافات والعلل، وندقق الإعراب الصرفي",
                        fontFamily = TajawalFontFamily,
                        fontSize = 12.sp,
                        color = Slate500,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.sp
                    )
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Gold500,
                        trackColor = Slate100
                    )
                }
            }
        }

        // ================= نتائج التحليل =================
        analysisData?.let { data ->
            val meterRes = data.meterResult

            // القسم أ: بطاقة البحر والتفاعيل (Royal900)
            if (optMeter) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_meter_section"),
                    shape = RoundedCornerShape(16.dp),
                    color = Royal900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Royal800),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // الرأس: شارة البحر، شارة السلامة، الأيقونة
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Gold500.copy(alpha = 0.20f))
                                            .border(1.dp, Gold400.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "البحر العروضي",
                                            fontFamily = TajawalFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Gold300,
                                            letterSpacing = 0.sp
                                        )
                                    }

                                    if (meterRes.isSound) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Emerald700.copy(alpha = 0.30f))
                                                .border(1.dp, Emerald300.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = null,
                                                    tint = Emerald300,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "موزون وسليم عروضياً",
                                                    fontFamily = TajawalFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Emerald300,
                                                    letterSpacing = 0.sp
                                                )
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Red600.copy(alpha = 0.30f))
                                                .border(1.dp, Gold400.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Gold400,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "الوزن مكسور",
                                                    fontFamily = TajawalFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Gold300,
                                                    letterSpacing = 0.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = meterRes.fullMeterTitle,
                                    fontFamily = TajawalFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    letterSpacing = 0.sp
                                )

                                if (meterRes.secondaryMatches.isNotEmpty()) {
                                    val secText = meterRes.secondaryMatches.joinToString(" ، ") { "بحر ${it.meterName} (${it.formType})" }
                                    Text(
                                        text = "احتمالات أخرى مقبولة: $secText",
                                        fontFamily = TajawalFontFamily,
                                        fontSize = 11.sp,
                                        color = Gold300,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Navy900.copy(alpha = 0.60f))
                                    .border(1.dp, Royal700, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // تفعيلات الشطر الأول (الصدر)
                        ShatrFeetCard(shatr = meterRes.sadrMatch, symbolsMode = symbolsMode)

                        // تفعيلات الشطر الثاني (العجز)
                        meterRes.ajuzMatch?.let { ajuz ->
                            ShatrFeetCard(shatr = ajuz, symbolsMode = symbolsMode)
                        }

                        // صندوق العروض والضرب
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Navy900.copy(alpha = 0.65f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Royal800.copy(alpha = 0.8f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${meterRes.sadrMatch.endFootTitle}: ${meterRes.sadrMatch.endFootName} (${meterRes.sadrMatch.endFootDesc})",
                                    fontFamily = TajawalFontFamily,
                                    fontSize = 12.sp,
                                    color = Slate200,
                                    letterSpacing = 0.sp
                                )
                                meterRes.ajuzMatch?.let { ajuz ->
                                    Text(
                                        text = "${ajuz.endFootTitle}: ${ajuz.endFootName} (${ajuz.endFootDesc})",
                                        fontFamily = TajawalFontFamily,
                                        fontSize = 12.sp,
                                        color = Slate200,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }
                        }

                        // تنبيه الكسر وتصحيح الوزن (إن وجد كسر)
                        if (!meterRes.isSound && meterRes.correction != null) {
                            val corr = meterRes.correction
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Navy900.copy(alpha = 0.85f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Gold400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "تنبيه الكسر العروضي وأقرب البحور:",
                                            fontFamily = TajawalFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Gold300,
                                            letterSpacing = 0.sp
                                        )
                                    }
                                    Text(
                                        text = corr.explanation,
                                        fontFamily = TajawalFontFamily,
                                        fontSize = 12.sp,
                                        color = Slate200,
                                        lineHeight = 18.sp,
                                        letterSpacing = 0.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        corr.closestMeters.forEach { cm ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Navy800)
                                                    .padding(6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "بحر ${cm.name}\n(فارق ${cm.distance})",
                                                    fontFamily = TajawalFontFamily,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Gold300,
                                                    textAlign = TextAlign.Center,
                                                    letterSpacing = 0.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // صندوق الكتابة العروضية والرموز الصوتية
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Navy900.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Royal800)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = Gold400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "الكتابة العروضية والرموز الصوتية",
                                            fontFamily = TajawalFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Gold400,
                                            letterSpacing = 0.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Royal800)
                                            .clickable { viewModel.toggleSymbolsMode() }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "نمط: $symbolsMode",
                                            fontFamily = TajawalFontFamily,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            letterSpacing = 0.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "الصدر: ${data.sadrAnalysis.phoneticLine}",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 17.sp,
                                    color = Royal100,
                                    lineHeight = 24.sp,
                                    letterSpacing = 0.sp
                                )
                                data.ajuzAnalysis?.let { ajuz ->
                                    Text(
                                        text = "العجز: ${ajuz.phoneticLine}",
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 17.sp,
                                        color = Royal100,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.sp
                                    )
                                }
                                Text(
                                    text = meterRes.prosodicExplanation,
                                    fontFamily = TajawalFontFamily,
                                    fontSize = 11.sp,
                                    color = Slate300,
                                    letterSpacing = 0.sp
                                )
                            }
                        }

                        // أزرار حفظ ونسخ التحليل
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.saveCurrentVerse() },
                                modifier = Modifier.testTag("btn_save_analysis"),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Royal700),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Gold300
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "حفظ في المحفوظات",
                                        fontFamily = TajawalFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = {
                                    val fullSummary = "تحليل ميزان:\n${data.vocalizedSadr} ... ${data.vocalizedAjuz}\nالبحر: ${meterRes.fullMeterTitle}\nالعروض: ${meterRes.sadrMatch.endFootName}\n${meterRes.prosodicExplanation}"
                                    copyToClipboard(fullSummary, "تم نسخ التحليل كاملاً إلى الحافظة ✓")
                                },
                                modifier = Modifier.testTag("btn_copy_analysis"),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Royal700),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Royal100
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "نسخ التحليل",
                                        fontFamily = TajawalFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // القسم ب: بطاقة الضبط والتشكيل (White Card)
            if (optDiacritics) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_diacritics_section"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الضبط الكامل والتشكيل المصحح",
                                fontFamily = TajawalFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Navy900,
                                letterSpacing = 0.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        if (!isEditingTashkeel) {
                                            editableTashkeelText = if (data.vocalizedAjuz.isNotEmpty()) "${data.vocalizedSadr} ... ${data.vocalizedAjuz}" else data.vocalizedSadr
                                            viewModel.setEditingTashkeel(true)
                                        } else {
                                            viewModel.setEditingTashkeel(false)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = Royal800,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isEditingTashkeel) "إلغاء التعديل" else "تعديل التشكيل",
                                            fontFamily = TajawalFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900,
                                            letterSpacing = 0.sp
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        val v = if (data.vocalizedAjuz.isNotEmpty()) "${data.vocalizedSadr} ... ${data.vocalizedAjuz}" else data.vocalizedSadr
                                        copyToClipboard(v, "تم نسخ البيت الشعري المضبوط ✓")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "نسخ البيت",
                                        tint = Royal800,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        if (!isEditingTashkeel) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Royal100)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = data.vocalizedSadr,
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Navy900,
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.sp,
                                        lineHeight = 32.sp
                                    )

                                    if (data.vocalizedAjuz.isNotEmpty()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = "✤", color = Gold600, fontSize = 12.sp)
                                            Box(
                                                modifier = Modifier
                                                    .width(64.dp)
                                                    .height(1.dp)
                                                    .background(Gold400)
                                            )
                                            Text(text = "✤", color = Gold600, fontSize = 12.sp)
                                        }

                                        Text(
                                            text = data.vocalizedAjuz,
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = Navy900,
                                            textAlign = TextAlign.Center,
                                            letterSpacing = 0.sp,
                                            lineHeight = 32.sp
                                        )
                                    }

                                    data.grammarData?.let { gd ->
                                        if (gd.poet != null || gd.era != null || gd.rhyme != null) {
                                            val parts = listOfNotNull(gd.poet, gd.era?.let { "العصر $it" }, gd.rhyme?.let { "القافية: $it" })
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color.White)
                                                    .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = parts.joinToString(" • "),
                                                    fontFamily = TajawalFontFamily,
                                                    fontSize = 11.sp,
                                                    color = Slate700,
                                                    letterSpacing = 0.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editableTashkeelText,
                                    onValueChange = { editableTashkeelText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 20.sp,
                                        color = Navy900,
                                        letterSpacing = 0.sp,
                                        lineHeight = 30.sp
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 3
                                )
                                Button(
                                    onClick = {
                                        viewModel.runAnalysis(editableTashkeelText)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold600)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "إعادة التحليل بالتشكيل الجديد",
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

            // القسم ج: الإعراب والنحو (White Card)
            if (optGrammar && data.grammarData != null) {
                val gd = data.grammarData
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_grammar_section"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountTree,
                                    contentDescription = null,
                                    tint = Royal800,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "الإعراب والسلامة النحوية",
                                    fontFamily = TajawalFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Navy900,
                                    letterSpacing = 0.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (gd.hasError) Red50 else Emerald50)
                                    .border(1.dp, if (gd.hasError) Red600.copy(alpha = 0.3f) else Emerald300.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (gd.hasError) "فيه ملاحظات لغوية" else "سليم لغوياً",
                                    fontFamily = TajawalFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (gd.hasError) Red600 else Emerald700,
                                    letterSpacing = 0.sp
                                )
                            }
                        }

                        Text(
                            text = "تحليل نحوي بالذكاء الاصطناعي وقد يخطئ",
                            fontFamily = TajawalFontFamily,
                            fontSize = 11.sp,
                            color = Slate500,
                            letterSpacing = 0.sp
                        )

                        // شبكة الكلمات المعربة
                        gd.words.forEach { w ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = w.word,
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 19.sp,
                                            color = Navy900,
                                            letterSpacing = 0.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Royal50)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = w.role,
                                                fontFamily = TajawalFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Royal800,
                                                letterSpacing = 0.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = w.explanation,
                                        fontFamily = TajawalFontFamily,
                                        fontSize = 12.sp,
                                        color = Slate600,
                                        lineHeight = 16.sp,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }
                        }

                        // لافتة خلاصة السلامة النحوية
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Royal50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Royal100)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Royal800,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = gd.summary,
                                    fontFamily = TajawalFontFamily,
                                    fontSize = 12.sp,
                                    color = Navy900,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun OptionCheckboxCard(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Surface(
        modifier = modifier
            .testTag(tag)
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(10.dp),
        color = if (checked) Royal50 else Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (checked) Royal700.copy(alpha = 0.4f) else Slate200)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(20.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Gold600,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontFamily = TajawalFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Navy900,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
fun ShatrFeetCard(
    shatr: com.example.engine.ShatrMeterMatch,
    symbolsMode: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Navy900.copy(alpha = 0.70f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Royal800.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = shatr.shatrTitle,
                    fontFamily = TajawalFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Gold400,
                    letterSpacing = 0.sp
                )
                Text(
                    text = shatr.feetCountText,
                    fontFamily = TajawalFontFamily,
                    fontSize = 11.sp,
                    color = Slate300,
                    letterSpacing = 0.sp
                )
            }

            Text(
                text = shatr.text,
                fontFamily = AmiriFontFamily,
                fontSize = 20.sp,
                color = Color.White,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
            )

            // شبكة التفعيلات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                shatr.feet.forEach { foot ->
                    val displayPattern = if (symbolsMode == "// o") {
                        foot.pattern.replace('1', '/').replace('0', 'o')
                    } else {
                        foot.pattern
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy800)
                            .border(1.dp, Royal700, RoundedCornerShape(8.dp))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = foot.name,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Gold400,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.sp
                            )
                            Text(
                                text = displayPattern,
                                fontFamily = TajawalFontFamily,
                                fontSize = 11.sp,
                                color = Slate300,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.sp
                            )
                            Text(
                                text = foot.description,
                                fontFamily = TajawalFontFamily,
                                fontSize = 9.sp,
                                color = Slate400,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
