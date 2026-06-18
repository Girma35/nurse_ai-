package com.nursyai.ai.assistant

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumAiEntitlements(context: Context) {
    private val prefs = context.getSharedPreferences("premium_ai", Context.MODE_PRIVATE)
    private val _isPremium = MutableStateFlow(prefs.getBoolean(KEY_IS_PREMIUM, false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun setPremium(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PREMIUM, enabled).apply()
        _isPremium.value = enabled
    }

    private companion object {
        const val KEY_IS_PREMIUM = "is_premium"
    }
}
