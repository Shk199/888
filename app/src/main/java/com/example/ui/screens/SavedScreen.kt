package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavedVerse
import com.example.ui.AnalyzerViewModel
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red200
import com.example.ui.theme.Red50
import com.example.ui.theme.Red600
import com.example.ui.theme.Royal100
import com.example.ui.theme.Royal50
import com.example.ui.theme.Royal700
import com.example.ui.theme.Royal800
import com.example.ui.theme.Royal900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.TajawalFontFamily

@Composable
fun SavedScreen(
    viewModel: AnalyzerViewModel,
    onOpenVerse: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val savedList by viewModel.savedVerses.collectAsState(initial = emptyList())

    val copyToClipboard = { text: String, msg: String ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Verse", text)
        clipboard.setPrimaryClip(clip)
        onShowToast(msg)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("tab_saved_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // رأس قسم الأبيات المحفوظة
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "الأبيات والتحليلات المحفوظة",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Navy900,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = "سجل الأبيات التي قمت بتحليلها مسبقاً",
                        fontFamily = TajawalFontFamily,
                        fontSize = 12.sp,
                        color = Slate500,
                        letterSpacing = 0.sp
                    )
                }

                if (savedList.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { viewModel.clearAllSaved() },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Red200),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Red50,
                            contentColor = Red600
                        )
                    ) {
                        Text(
                            text = "حذف الكل",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }
        }

        // الحالة الفارغة أو القائمة
        if (savedList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Slate100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmarks,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "لا توجد أبيات محفوظة بعد",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Navy900,
                            letterSpacing = 0.sp
                        )
                        Text(
                            text = "عند تحليل أي بيت شعري، اضغط \"حفظ في المحفوظات\" للرجوع إليه هنا في أي وقت.",
                            fontFamily = TajawalFontFamily,
                            fontSize = 12.sp,
                            color = Slate500,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }
        } else {
            items(savedList) { item ->
                SavedVerseCard(
                    item = item,
                    onOpen = { onOpenVerse(item.verse) },
                    onCopy = { copyToClipboard(item.verse, "تم نسخ البيت إلى الحافظة ✓") },
                    onDelete = { viewModel.deleteSavedVerse(item.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SavedVerseCard(
    item: SavedVerse,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.meterName,
                    fontFamily = TajawalFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Navy900,
                    letterSpacing = 0.sp
                )
                Text(
                    text = item.date,
                    fontFamily = TajawalFontFamily,
                    fontSize = 10.sp,
                    color = Slate400,
                    letterSpacing = 0.sp
                )
            }

            Text(
                text = item.verse,
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Navy900,
                lineHeight = 26.sp,
                letterSpacing = 0.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Royal800,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "فتح",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Navy900,
                            letterSpacing = 0.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Royal800,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "نسخ",
                            fontFamily = TajawalFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Navy900,
                            letterSpacing = 0.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Red600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
