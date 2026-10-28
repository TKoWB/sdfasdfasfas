package com.example.button.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class AdsConsentManager(context: Context) {
    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(context.applicationContext)

    var canRequestAds by mutableStateOf(consentInformation.canRequestAds())
        private set

    var privacyOptionsRequired by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set
    private var adsInitialized = false

    fun requestConsent(activity: Activity) {
        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                updateConsentState()
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    updateConsentState()
                    if (error != null) {
                        message = "Không tải được biểu mẫu quyền riêng tư: ${error.message}"
                        Log.w("AdsConsentManager", message.orEmpty())
                    }
                    initializeAdsIfAllowed(activity)
                }
            },
            { error ->
                updateConsentState()
                message = "Không cập nhật được trạng thái đồng ý quảng cáo: ${error.message}"
                Log.w("AdsConsentManager", message.orEmpty())
                initializeAdsIfAllowed(activity)
            }
        )

        updateConsentState()
        initializeAdsIfAllowed(activity)
    }

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            updateConsentState()
            if (error != null) {
                message = "Không mở được tùy chọn quyền riêng tư: ${error.message}"
                Log.w("AdsConsentManager", message.orEmpty())
            } else {
                message = null
            }
            initializeAdsIfAllowed(activity)
        }
    }

    private fun initializeAdsIfAllowed(activity: Activity) {
        if (canRequestAds && !adsInitialized) {
            adsInitialized = true
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(TEST_DEVICE_ID))
                    .build()
            )
            MobileAds.initialize(activity) {}
        }
    }

    private fun updateConsentState() {
        canRequestAds = consentInformation.canRequestAds()
        privacyOptionsRequired =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    private companion object {
        const val TEST_DEVICE_ID = "092C9283A174BD403849B576A7D339FA"
    }
}
