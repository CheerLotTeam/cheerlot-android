package com.gms.cheerlotandroid.domain.service.analytics

interface AnalyticsService {
    fun track(event: AnalyticsEvent)
    fun setUserProperty(key: AnalyticsUserProperty, value: Any)
    fun incrementUserProperty(key: AnalyticsUserProperty)
}
