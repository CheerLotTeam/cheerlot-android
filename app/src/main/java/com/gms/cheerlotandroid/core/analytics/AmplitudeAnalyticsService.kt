package com.gms.cheerlotandroid.core.analytics

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.core.events.Identify
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsUserProperty

class AmplitudeAnalyticsService(context: Context, apiKey: String) : AnalyticsService {
    // 로그인 계정이 없으므로 userId를 따로 지정하지 않고 Amplitude 기본 deviceId를 사용합니다.
    // 로컬 Key가 없는 환경에서도 앱 기능은 유지하고 분석 전송만 생략합니다.
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
