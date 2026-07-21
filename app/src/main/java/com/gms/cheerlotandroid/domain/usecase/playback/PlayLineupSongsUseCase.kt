package com.gms.cheerlotandroid.domain.usecase.playback

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer

// 라인업 화면에서 선수 응원가를 순서대로 재생 큐에 올립니다.
class PlayLineupSongsUseCase(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(
        songs: List<CheerSongInfo>,
        playerNames: List<String>,
        startAt: Int
    ) {
        audioPlayer.playQueue(
            songs = songs,
            playerNames = playerNames,
            startAt = startAt,
            teamId = null,
            mode = PlaybackMode.LINEUP
        )
    }
}
