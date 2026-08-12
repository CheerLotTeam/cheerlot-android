package com.gms.cheerlotandroid.domain.service.remoteconfig

interface RemoteConfigService {
    suspend fun fetch(): RemoteAppConfig
}
