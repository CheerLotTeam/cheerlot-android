package com.gms.cheerlotandroid.core.analytics

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.core.events.Identify
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsUserProperty

class AmplitudeAnalyticsService(context: Context, apiKey: String) : AnalyticsService {
    private val amplitude: Amplitude? = apiKey.takeIf(String::isNotBlank)?.let {
        Amplitude(Configuration(apiKey = it, context = context.applicationContext))
    }

    override fun track(event: AnalyticsEvent) {
        amplitude?.track(eventType = event.name, eventProperties = event.properties)
    }

    override fun setUserProperty(key: AnalyticsUserProperty, value: Any) {
        amplitude?.identify(Identify().set(key.value, value))
    }

    override fun incrementUserProperty(key: AnalyticsUserProperty) {
        amplitude?.identify(Identify().add(key.value, 1))
    }
}
