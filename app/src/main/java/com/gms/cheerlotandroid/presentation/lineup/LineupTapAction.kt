package com.gms.cheerlotandroid.presentation.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo

// 선수 Cell을 탭했을 때 응원가 개수에 따라 어디로 갈지를 순수 계산으로 결정합니다 (실제 네비게이션은 화면이 수행).
sealed interface LineupTapAction {
    // 응원가 2개 이상 → 응원가 선택 시트로
    data class ShowSongList(
        val member: PlayerInfo,
        val startIndex: Int
    ) : LineupTapAction

    // 응원가 정확히 1개 → 바로 재생
    data class GoToPlayback(val startIndex: Int) : LineupTapAction

    // 응원가 0개 → 토스트만 노출
    data object ShowNoSongToast : LineupTapAction
}
