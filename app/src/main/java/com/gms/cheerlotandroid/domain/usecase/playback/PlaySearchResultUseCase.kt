package com.gms.cheerlotandroid.domain.usecase.playback

import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

// 검색 결과를 탭했을 때는 iOS PlaySearchSongsUseCaseImpl과 동일하게, 전체 로스터가 아니라
// 화면에 지금 보이는 결과 중 같은 선수의 곡들만 큐로 묶어서 PlaybackMode.SEARCH로 재생합니다.
class PlaySearchResultUseCase(
    private val audioPlayer: AudioPlayer
) {
    fun play(
        selectedRow: TeamMembersRow,
        displayedRows: List<TeamMembersRow>,
        teamId: TeamId,
        isGameDay: Boolean,
    ) {
        val playableRows = displayedRows.filter { it.hasSong && it.playerId == selectedRow.playerId }
        if (playableRows.isEmpty()) return
        val startAt = playableRows.indexOf(selectedRow).takeIf { it >= 0 } ?: 0

        audioPlayer.playQueue(
            songs = playableRows.mapNotNull { it.song },
            playerNames = playableRows.map { it.playerName },
            startAt = startAt,
            teamId = teamId,
            mode = PlaybackMode.SEARCH,
            playerIds = playableRows.map { it.playerId.value },
            isGameDay = isGameDay,
        )
    }
}
