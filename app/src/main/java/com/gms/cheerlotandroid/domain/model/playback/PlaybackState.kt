package com.gms.cheerlotandroid.domain.model.playback

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.analytics.PlaySource

data class PlaybackState(
    val nowPlaying: CheerSongInfo? = null,
    val currentPlayerName: String? = null,
    val currentPlayerId: String = "",
    val source: PlaySource = PlaySource.TEAM_MEMBERS,
    val isGameDay: Boolean = false,
    val teamId: TeamId? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackMode: PlaybackMode = PlaybackMode.NORMAL,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val currentQueueIndex: Int = 0,
    val queueSize: Int = 0
) {
    // 검색모드는 수동 스킵을 허용하지 않습니다(끝나면 같은 곡을 반복). NORMAL/LINEUP은 큐가 2개 이상이면 허용.
    // 단, LINEUP의 이전곡/다음곡 경계 동작(3초 되감기, wrap 여부)은 AudioPlaybackPlayer에서 모드별로 따로 처리합니다.
    val canSkipManually: Boolean
        get() = playbackMode != PlaybackMode.SEARCH && queueSize > 1
}
