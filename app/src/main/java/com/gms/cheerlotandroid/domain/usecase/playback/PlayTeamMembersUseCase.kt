package com.gms.cheerlotandroid.domain.usecase.playback

import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRowVO

class PlayTeamMembersUseCase(
    private val audioPlayer: AudioPlayer
) {
    fun playAll(rows: List<TeamMembersRowVO>, teamId: TeamId) {
        play(rows = rows, teamId = teamId, selectedRow = null)
    }

    fun playSelected(
        row: TeamMembersRowVO,
        allRows: List<TeamMembersRowVO>,
        teamId: TeamId
    ) {
        play(rows = allRows, teamId = teamId, selectedRow = row)
    }

    private fun play(rows: List<TeamMembersRowVO>, teamId: TeamId, selectedRow: TeamMembersRowVO?) {
        val playableRows = rows.filter { it.hasSong }
        if (playableRows.isEmpty()) return
        val startAt = selectedRow?.let { playableRows.indexOf(it).takeIf { index -> index >= 0 } } ?: 0

        audioPlayer.playQueue(
            songs = playableRows.mapNotNull { it.song },
            playerNames = playableRows.map { it.playerName },
            startAt = startAt,
            teamId = teamId,
            mode = PlaybackMode.NORMAL
        )
    }
}
