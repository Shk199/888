package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppTab
import com.example.ui.components.BottomNavBar
import com.example.ui.components.HeaderBar
import com.example.ui.screens.AnalyzerScreen
import com.example.ui.screens.MetersScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.Navy900
import com.example.ui.theme.TajawalFontFamily
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: AnalyzerViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.ANALYZER) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { msg ->
            toastMessage = msg
            delay(2500)
            toastMessage = null
        }
    }

    // BackHandler: Return to ANALYZER if on another tab
    BackHandler(enabled = currentTab != AppTab.ANALYZER) {
        currentTab = AppTab.ANALYZER
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = { HeaderBar() },
            bottomBar = {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    AppTab.ANALYZER -> {
                        AnalyzerScreen(
                            viewModel = viewModel,
                            onShowToast = { msg ->
                                toastMessage = msg
                            }
                        )
                    }
                    AppTab.METERS -> {
                        MetersScreen(
                            onTryMeter = { verse ->
                                viewModel.setInputText(verse)
                                currentTab = AppTab.ANALYZER
                                viewModel.runAnalysis(verse)
                            }
                        )
                    }
                    AppTab.SAVED -> {
                        SavedScreen(
                            viewModel = viewModel,
                            onOpenVerse = { verse ->
                                viewModel.setInputText(verse)
                                currentTab = AppTab.ANALYZER
                                viewModel.runAnalysis(verse)
                            },
                            onShowToast = { msg ->
                                toastMessage = msg
                            }
                        )
                    }
                }

                // Toast مخصص بتصميم ميزان الأنيق
                AnimatedVisibility(
                    visible = toastMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    toastMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy900)
                                .border(1.dp, Gold500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = msg,
                                fontFamily = TajawalFontFamily,
                                fontSize = 12.sp,
                                color = Gold400,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
