package com.gms.cheerlotandroid.domain.usecase.playback

import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

class PlayTeamMembersUseCase(
    private val audioPlayer: AudioPlayer
) {
    fun playAll(rows: List<TeamMembersRow>, teamId: TeamId, isGameDay: Boolean) {
        play(rows = rows, teamId = teamId, selectedRow = null, isGameDay = isGameDay)
    }

    fun playSelected(
        row: TeamMembersRow,
        allRows: List<TeamMembersRow>,
        teamId: TeamId,
        isGameDay: Boolean,
    ) {
        play(rows = allRows, teamId = teamId, selectedRow = row, isGameDay = isGameDay)
    }

    private fun play(
        rows: List<TeamMembersRow>,
        teamId: TeamId,
        selectedRow: TeamMembersRow?,
        isGameDay: Boolean,
    ) {
        val playableRows = rows.filter { it.hasSong }
        if (playableRows.isEmpty()) return
        val startAt = selectedRow?.let { playableRows.indexOf(it).takeIf { index -> index >= 0 } } ?: 0

        audioPlayer.playQueue(
            songs = playableRows.mapNotNull { it.song },
            playerNames = playableRows.map { it.playerName },
            startAt = startAt,
            teamId = teamId,
            mode = PlaybackMode.NORMAL,
            playerIds = playableRows.map { it.playerId.value },
            isGameDay = isGameDay,
        )
    }
}
