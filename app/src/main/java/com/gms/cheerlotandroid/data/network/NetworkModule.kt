package com.gms.cheerlotandroid.data.network

import com.gms.cheerlotandroid.BuildConfig
import com.gms.cheerlotandroid.data.network.service.PlayerApiService
import com.gms.cheerlotandroid.data.network.service.TeamApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// 네트워크 관련 의존성은 여기서 한 번 생성해 AppContainer로 전달합니다.
// 전부 by lazy라서, 실제 OkHttpClient/Retrofit 인스턴스는 첫 API 호출 시점에 만들어집니다.
class NetworkModule {
    // 디버그 빌드에서만 요청/응답 바디를 로그캣에 찍음
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        // Retrofit은 baseUrl이 "/"로 안 끝나면 예외를 던지므로 여기서 보정
        val baseUrl = BuildConfig.API_BASE_URL.let { if (it.endsWith("/")) it else "$it/" }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // retrofit.create()가 인터페이스 애노테이션을 보고 실제 HTTP 호출을 하는 구현체를 즉석에서 만들어줍니다.
    val playerApiService: PlayerApiService by lazy { retrofit.create(PlayerApiService::class.java) }
    val teamApiService: TeamApiService by lazy { retrofit.create(TeamApiService::class.java) }
}
