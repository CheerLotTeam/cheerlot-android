package com.gms.cheerlotandroid.core.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.gms.cheerlotandroid.domain.service.remoteconfig.RemoteAppConfig
import com.gms.cheerlotandroid.domain.service.remoteconfig.RemoteConfigService
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseRemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance(),
) : RemoteConfigService {
    init {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0L)
                .build()
        )
        remoteConfig.setDefaultsAsync(
            mapOf(
                MINIMUM_VERSION_KEY to DEFAULT_MINIMUM_VERSION,
                SERVER_CHECK_KEY to false,
                SERVER_CHECK_MESSAGE_KEY to DEFAULT_SERVER_CHECK_MESSAGE,
            )
        )
    }

    override suspend fun fetch(): RemoteAppConfig {
        return suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (continuation.isActive) continuation.resume(currentConfig())
            }
        }
    }

    private fun currentConfig() = RemoteAppConfig(
        minimumVersion = remoteConfig.getString(MINIMUM_VERSION_KEY)
            .ifBlank { DEFAULT_MINIMUM_VERSION },
        isServerChecking = remoteConfig.getBoolean(SERVER_CHECK_KEY),
        serverCheckingMessage = remoteConfig.getString(SERVER_CHECK_MESSAGE_KEY)
            .ifBlank { DEFAULT_SERVER_CHECK_MESSAGE },
    )

    private companion object {
        const val MINIMUM_VERSION_KEY = "android_minimum_version"
        const val SERVER_CHECK_KEY = "is_server_check"
        const val SERVER_CHECK_MESSAGE_KEY = "server_check_message"
        const val DEFAULT_MINIMUM_VERSION = "1.0.0"
        const val DEFAULT_SERVER_CHECK_MESSAGE = "서버 점검 중입니다."
    }
}
