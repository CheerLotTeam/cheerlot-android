package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.team.TeamId

// Bottom Sheet로 표시할 modal 흐름입니다.
//
// 새 sheet를 추가할 때의 순서:
// 1. 이 파일에 sheet 타입을 data object 또는 data class로 추가합니다.
// 2. sheet를 열어야 하는 화면에서 navigator.showSheet(...)를 호출합니다.
// 3. 실제 ModalBottomSheet UI 연결은 root ModalHost에서 currentSheet를 기준으로 처리합니다.
// 4. sheet가 닫힐 때는 navigator.dismissSheet() 또는 navigator.dismissModal()을 호출합니다.
//
// sheet와 full screen은 동시에 표시하지 않도록 Navigator에서 서로를 정리합니다.
sealed interface CheerLotSheet {
    data class CheerSongList(
        val playerId: String,
    ) : CheerLotSheet

    data class LineupChange(
        val playerId: String,
    ) : CheerLotSheet

    data class TeamChange(
        val selectedTeamId: TeamId,
    ) : CheerLotSheet

    data object Inquiry : CheerLotSheet

    data object ServicePage : CheerLotSheet
}

/*
새 sheet를 추가하는 예시:

1. argument가 없는 sheet

data object AppIconSelect : CheerLotSheet

사용:

navigator.showSheet(CheerLotSheet.AppIconSelect)

2. argument가 있는 sheet

data class CheerSongOption(
    val cheerSongId: String,
) : CheerLotSheet

사용:

navigator.showSheet(
    CheerLotSheet.CheerSongOption(cheerSongId = "playerId_songTitle")
)

root ModalHost에서 연결하는 예시:

when (val sheet = navigator.currentSheet) {
    CheerLotSheet.AppIconSelect -> AppIconSelectSheet(...)
    is CheerLotSheet.CheerSongOption -> CheerSongOptionSheet(cheerSongId = sheet.cheerSongId)
    null -> Unit
}
*/
