package com.gms.cheerlotandroid.domain.service.remoteconfig

data class RemoteAppConfig(
    val minimumVersion: String = "1.0.0",
    val isServerChecking: Boolean = false,
    val serverCheckingMessage: String = "안정적인 서비스 제공을 위해 시스템 점검 중입니다.",
)
