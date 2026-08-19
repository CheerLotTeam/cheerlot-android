package com.gms.cheerlotandroid.core.remoteconfig

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.gms.cheerlotandroid.domain.service.remoteconfig.RemoteAppConfig
import com.gms.cheerlotandroid.domain.service.remoteconfig.RemoteConfigService
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseRemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance(),
) : RemoteConfigService {
    override suspend fun fetch(): RemoteAppConfig {
        // 비동기 설정이 실제 fetch보다 먼저 끝나도록 순서를 보장합니다.
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0L)
                .build()
        ).awaitIsSuccessful()
        remoteConfig.setDefaultsAsync(
            mapOf(
                MINIMUM_VERSION_KEY to DEFAULT_MINIMUM_VERSION,
                SERVER_CHECK_KEY to false,
                SERVER_CHECK_MESSAGE_KEY to DEFAULT_SERVER_CHECK_MESSAGE,
            )
        ).awaitIsSuccessful()

        val isFetchSuccessful = remoteConfig.fetchAndActivate().awaitIsSuccessful()
        // 실패 시 이전 활성값을 재사용하지 않고 앱 기본값으로 정상 진입합니다.
        return if (isFetchSuccessful) currentConfig() else RemoteAppConfig()
    }

    private suspend fun Task<*>.awaitIsSuccessful(): Boolean =
        suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (continuation.isActive) continuation.resume(task.isSuccessful)
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
