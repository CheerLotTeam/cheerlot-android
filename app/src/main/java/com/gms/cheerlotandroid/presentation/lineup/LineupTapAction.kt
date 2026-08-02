package com.gms.cheerlotandroid.presentation.lineup

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo

// 선수 Cell을 탭했을 때 응원가 개수에 따라 어디로 갈지를 순수 계산으로 결정합니다 (실제 네비게이션은 화면이 수행).
sealed interface LineupTapAction {
    // 응원가 2개 이상 → 응원가 선택 시트로.
    // 시트에서 곡을 고르는 시점에 큐를 새로 만들 수 없으므로(CheerLotModalHost는 LineupViewModel에
    // 접근할 수 없음), 전체 라인업 큐(queueSongs/queuePlayerNames)를 함께 들고 갑니다.
    data class ShowSongList(
        val member: PlayerInfo,
        val startIndex: Int,
        val queueSongs: List<CheerSongInfo>,
        val queuePlayerNames: List<String>
    ) : LineupTapAction

    // 응원가 정확히 1개 → 바로 재생
    data class GoToPlayback(val startIndex: Int) : LineupTapAction

    // 응원가 0개 → 토스트만 노출
    data object ShowNoSongToast : LineupTapAction
}
