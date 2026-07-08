package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.team.TeamId

// 전체 화면으로 표시할 modal 흐름입니다.
//
// 새 full screen 흐름을 추가할 때의 순서:
// 1. 이 파일에 full screen 타입을 data object 또는 data class로 추가합니다.
// 2. 화면에서 navigator.showFullScreen(...)을 호출합니다.
// 3. 실제 전체 화면 UI 연결은 root ModalHost에서 currentFullScreen을 기준으로 처리합니다.
// 4. 닫을 때는 navigator.dismissFullScreen() 또는 navigator.dismissModal()을 호출합니다.
//
// 일반 push 화면이 아니라 재생 화면처럼 현재 흐름 위를 덮는 화면에 사용합니다.
sealed interface CheerLotFullScreen {
    data class LineupPlayback(
        val startIndex: Int,
    ) : CheerLotFullScreen

    data class BasePlayback(
        val teamId: TeamId,
        val cheerSongId: String,
        val playerName: String,
        val source: PlaybackSource,
    ) : CheerLotFullScreen
}

enum class PlaybackSource {
    BASE,
    LINEUP
}

/*
새 full screen modal을 추가하는 예시:

data class TeamPreview(
    val teamId: TeamId,
) : CheerLotFullScreen

사용:

navigator.showFullScreen(
    CheerLotFullScreen.TeamPreview(teamId = TeamId("SAMSUNG"))
)

root ModalHost에서 연결하는 예시:

when (val fullScreen = navigator.currentFullScreen) {
    is CheerLotFullScreen.LineupPlayback -> LineupPlaybackScreen(startIndex = fullScreen.startIndex)
    is CheerLotFullScreen.BasePlayback -> PlaybackScreen(
        teamId = fullScreen.teamId,
        cheerSongId = fullScreen.cheerSongId,
        playerName = fullScreen.playerName,
        source = fullScreen.source,
    )
    is CheerLotFullScreen.TeamPreview -> TeamPreviewScreen(teamId = fullScreen.teamId)
    null -> Unit
}

기준:
- 현재 화면 위를 완전히 덮는 재생/미리보기 흐름에 사용합니다.
- 일반 push 화면은 CheerLotRoute에 추가합니다.
*/
