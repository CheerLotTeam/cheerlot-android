package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo

// Bottom Sheet로 표시할 modal 흐름입니다.
// sheet와 full screen은 동시에 표시하지 않도록 Navigator에서 서로를 정리합니다.
sealed interface CheerLotSheet {
    // queueSongs/queuePlayerNames: 선수 하나가 아니라 전체 라인업 큐입니다. 이 화면(root)은
    // LineupViewModel에 접근할 수 없어서, 곡 선택 시 재생을 시작하려면 큐 전체가 필요합니다.
    data class CheerSongList(
        val member: PlayerInfo,
        val startIndex: Int,
        val queueSongs: List<CheerSongInfo>,
        val queuePlayerNames: List<String>,
        val queuePlayerIds: List<String>,
        val isGameDay: Boolean,
    ) : CheerLotSheet

    data class LineupChange(
        val member: PlayerInfo,
    ) : CheerLotSheet

    data object TeamChange : CheerLotSheet

    data object Inquiry : CheerLotSheet
}
