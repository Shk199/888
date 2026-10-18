package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy900
import com.example.ui.theme.Royal900
import com.example.ui.theme.Slate300
import com.example.ui.theme.TajawalFontFamily

@Composable
fun HeaderBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Navy900,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(width = 1.dp, color = Royal900.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle 36dp with Gold600 at 20% alpha and Gold500 border at 40% alpha
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gold600.copy(alpha = 0.20f))
                        .border(
                            width = 1.dp,
                            color = Gold500.copy(alpha = 0.40f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_mark),
                        contentDescription = "شعار ميزان",
                        modifier = Modifier.size(26.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "مُحَلِّلُ الشِّعْرِ العَرَبِيّ",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White,
                        letterSpacing = 0.sp,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "الناقد والمدقق العروضي الذكي",
                        fontFamily = TajawalFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = Slate300,
                        letterSpacing = 0.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
