package com.example.button

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.button.ads.AdsConsentManager
import com.example.button.premium.PremiumAccessManager
import com.example.button.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    private lateinit var premiumAccessManager: PremiumAccessManager
    private lateinit var adsConsentManager: AdsConsentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        premiumAccessManager = (application as ButtonApplication).premiumAccessManager
        adsConsentManager = AdsConsentManager(applicationContext)
        adsConsentManager.requestConsent(this)

        setContent {
            AppNavGraph(premiumAccessManager, adsConsentManager)
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            premiumAccessManager.resetSession()
        }
        super.onDestroy()
    }
}