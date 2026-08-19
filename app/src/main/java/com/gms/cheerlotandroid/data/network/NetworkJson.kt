package com.gms.cheerlotandroid.data.network

import kotlinx.serialization.json.Json

// NetworkModule(Retrofit 컨버터)과 NetworkError(에러 바디 파싱) 양쪽에서 같은 설정을 공유
// ignoreUnknownKeys=true라 서버가 필드를 추가해도 파싱 에러 없이 무시합니다.
internal val networkJson = Json { ignoreUnknownKeys = true }
