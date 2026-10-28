package com.example.button.ui.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay

@Composable
fun GlobalAdsHost(
    isPremium: Boolean,
    canRequestAds: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val latestIsPremium by rememberUpdatedState(isPremium)
    val latestCanRequestAds by rememberUpdatedState(canRequestAds)
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    var isAdLoading by remember { mutableStateOf(false) }
    var isAdShowing by remember { mutableStateOf(false) }
    var adTimerMillis by remember { mutableLongStateOf(0L) }
    var hasShownInitialAd by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var isForeground by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }

    fun loadInterstitialAd() {
        if (!latestCanRequestAds || latestIsPremium || isAdLoading || interstitialAd != null) {
            return
        }

        isAdLoading = true
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isAdLoading = false
                    interstitialAd = if (!latestIsPremium && latestCanRequestAds) ad else null
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoading = false
                    interstitialAd = null
                    Log.w("GlobalAds", "Interstitial failed to load: ${error.message}")
                }
            }
        )
    }

    LaunchedEffect(canRequestAds, isPremium) {
        if (!canRequestAds || isPremium) {
            interstitialAd = null
            return@LaunchedEffect
        }

        while (true) {
            loadInterstitialAd()
            delay(AD_LOAD_RETRY_MILLIS)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isForeground = true
                Lifecycle.Event.ON_PAUSE -> isForeground = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isPremium, canRequestAds, isForeground) {
        if (isPremium || !canRequestAds || !isForeground) return@LaunchedEffect

        while (true) {
            delay(AD_TIMER_TICK_MILLIS)
            if (isAdShowing) continue

            val intervalMillis = if (hasShownInitialAd) {
                AD_INTERVAL_MILLIS
            } else {
                INITIAL_AD_INTERVAL_MILLIS
            }
            adTimerMillis = (adTimerMillis + AD_TIMER_TICK_MILLIS)
                .coerceAtMost(intervalMillis)
            if (adTimerMillis < intervalMillis) continue

            val ad = interstitialAd ?: continue
            val activity = context.findActivity() ?: continue
            interstitialAd = null
            isAdShowing = true
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    hasShownInitialAd = true
                    adTimerMillis = 0L
                }

                override fun onAdDismissedFullScreenContent() {
                    isAdShowing = false
                    loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    isAdShowing = false
                    Log.w("GlobalAds", "Interstitial failed to show: ${error.message}")
                    loadInterstitialAd()
                }
            }
            ad.show(activity)
        }
    }

    if (canRequestAds && !isPremium) {
        BannerAd(modifier = modifier)
    }
}

@Composable
private fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adView = remember(context) {
        AdView(context).apply {
            adUnitId = TEST_BANNER_AD_UNIT_ID
            setAdSize(AdSize.BANNER)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.i("GlobalAds", "Test banner loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(
                        "GlobalAds",
                        "Test banner failed to load: ${error.message}; " +
                            "code=${error.code}, domain=${error.domain}, response=${error.responseInfo}"
                    )
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(adView) {
        onDispose {
            adView.destroy()
        }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val AD_INTERVAL_MILLIS = 3 * 60 * 1000L
private const val INITIAL_AD_INTERVAL_MILLIS = 5 * 1000L
private const val AD_TIMER_TICK_MILLIS = 1000L
private const val AD_LOAD_RETRY_MILLIS = 30 * 1000L
private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
