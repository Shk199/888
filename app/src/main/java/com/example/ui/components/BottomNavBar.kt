package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Gold400
import com.example.ui.theme.Navy900
import com.example.ui.theme.Royal900
import com.example.ui.theme.Slate300
import com.example.ui.theme.TajawalFontFamily

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    ANALYZER("المحلل", Icons.Default.AutoAwesome, "nav_tab_analyzer"),
    METERS("البحور", Icons.AutoMirrored.Filled.MenuBook, "nav_tab_meters"),
    SAVED("المحفوظات", Icons.Default.Bookmarks, "nav_tab_saved")
}

@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Navy900,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Navy900),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = (tab == selectedTab)
                val color = if (isSelected) Gold400 else Slate300

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .testTag(tab.tag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tab.title,
                        fontFamily = TajawalFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = color,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }
}
