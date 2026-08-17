package com.gms.cheerlotandroid.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// MiniPlayer를 탭했을 때 어느 풀스크린 재생 화면으로 돌아가야 하는지를 나타냅니다.
// 라인업 재생(PlaybackMode.LINEUP)은 미니플레이어로 이어지지 않으므로(아래 uiState 참고) 대상이 없습니다.
internal data class MiniPlayerReopenTarget(
    val teamId: TeamId,
    val cheerSongId: String,
    val playerName: String
)

// MainTabScreen 하단에 항상 떠 있는 MiniPlayer의 상태/액션을 audioPlayer.state에서 파생합니다.
internal class MiniPlayerViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val uiState: StateFlow<MiniPlayerUiState?> = audioPlayer.state
        .map { state ->
            // 라인업 재생은 iOS와 동일하게 Shorts 방식(피드를 나가면 정지)이라 미니플레이어로 이어지지 않습니다.
            if (state.playbackMode == PlaybackMode.LINEUP) return@map null
            val song = state.nowPlaying ?: return@map null
            val playerName = state.currentPlayerName.orEmpty()

            MiniPlayerUiState(
                playerName = playerName,
                songTitle = song.title,
                teamId = state.teamId,
                isPlaying = state.isPlaying
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onPlayClick() {
        audioPlayer.toggle()
    }

    fun onSkipNextClick() {
        audioPlayer.playNext()
    }

    // MiniPlayer를 탭했을 때 돌아갈 풀스크린 재생 화면 목적지를 계산합니다.
    fun reopenTarget(): MiniPlayerReopenTarget? {
        val state = audioPlayer.state.value
        val song = state.nowPlaying ?: return null
        val teamId = state.teamId ?: return null

        return MiniPlayerReopenTarget(
            teamId = teamId,
            cheerSongId = song.id,
            playerName = state.currentPlayerName.orEmpty()
        )
    }
}
