package com.example.button

import android.app.Application
import com.example.button.premium.PremiumAccessManager

class ButtonApplication : Application() {
    val premiumAccessManager by lazy { PremiumAccessManager() }
}
