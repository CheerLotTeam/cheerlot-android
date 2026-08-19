package com.gms.cheerlotandroid.domain.model.playback

// 재생 큐의 정책을 구분합니다. 모드별 셔플/반복/스킵 동작 차이는 AudioPlaybackPlayer에서 처리합니다.
enum class PlaybackMode {
    NORMAL,
    LINEUP,
    SEARCH
}
