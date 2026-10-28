package com.example.button.premium

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PremiumAccessManager {
    var isPremium by mutableStateOf(false)
        private set

    fun simulatePurchase() {
        isPremium = true
    }

    fun resetSession() {
        isPremium = false
    }
}
