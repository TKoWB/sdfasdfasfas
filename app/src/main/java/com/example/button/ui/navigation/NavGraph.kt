package com.example.button.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.button.ads.AdsConsentManager
import com.example.button.premium.PremiumAccessManager
import com.example.button.ui.ads.GlobalAdsHost
import com.example.button.ui.screens.MainScreen
import com.example.button.ui.screens.SpecialMusicListScreen

// Định nghĩa Tên các tuyến đường (Route)
object ScreenRoute {
    const val MAIN = "main_screen"
    const val SPECIAL_MUSIC_LIST = "special_music_list_screen"
}

@Composable
fun AppNavGraph(
    premiumAccessManager: PremiumAccessManager,
    adsConsentManager: AdsConsentManager
) {
    // NavController quản lý lịch sử chuyển màn hình (BackStack)
    val navController = rememberNavController()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.MAIN,
            modifier = Modifier.weight(1f)
        ) {
            composable(route = ScreenRoute.MAIN) {
                MainScreen(
                    isPremium = premiumAccessManager.isPremium,
                    privacyOptionsRequired = adsConsentManager.privacyOptionsRequired,
                    onPrivacyOptionsClick = {
                        context.findActivity()?.let(adsConsentManager::showPrivacyOptions)
                    },
                    onPurchasePremium = premiumAccessManager::simulatePurchase,
                    onNavigateToMusicList = {
                        navController.navigate(ScreenRoute.SPECIAL_MUSIC_LIST)
                    }
                )
            }

            composable(route = ScreenRoute.SPECIAL_MUSIC_LIST) {
                SpecialMusicListScreen(
                    isPremium = premiumAccessManager.isPremium,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        GlobalAdsHost(
            isPremium = premiumAccessManager.isPremium,
            canRequestAds = adsConsentManager.canRequestAds,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}