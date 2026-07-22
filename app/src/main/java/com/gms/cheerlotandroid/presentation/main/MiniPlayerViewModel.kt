package com.gms.cheerlotandroid.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.design.team.TeamAsset
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// MiniPlayer를 탭했을 때 어느 풀스크린 재생 화면으로 돌아가야 하는지를 나타냅니다.
internal sealed interface MiniPlayerReopenTarget {
    data class Lineup(val startIndex: Int) : MiniPlayerReopenTarget
    data class Base(
        val teamId: TeamId,
        val cheerSongId: String,
        val playerName: String
    ) : MiniPlayerReopenTarget
}

// MainTabScreen 하단에 항상 떠 있는 MiniPlayer의 상태/액션을 audioPlayer.state에서 파생합니다.
internal class MiniPlayerViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val uiState: StateFlow<MiniPlayerUiState?> = audioPlayer.state
        .map { state ->
            val song = state.nowPlaying ?: return@map null
            val playerName = state.currentPlayerName.orEmpty()

            MiniPlayerUiState(
                title = "$playerName · ${song.title}",
                teamInitial = state.teamId?.let { TeamAsset.from(it).assetPrefix.uppercase() }
                    ?: playerName.take(1),
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

    // MiniPlayer를 탭했을 때, 현재 재생 모드에 맞는 풀스크린 재생 화면 목적지를 계산합니다.
    fun reopenTarget(): MiniPlayerReopenTarget? {
        val state = audioPlayer.state.value
        val song = state.nowPlaying ?: return null

        return if (state.playbackMode == PlaybackMode.LINEUP) {
            MiniPlayerReopenTarget.Lineup(startIndex = state.currentQueueIndex)
        } else {
            val teamId = state.teamId ?: return null
            MiniPlayerReopenTarget.Base(
                teamId = teamId,
                cheerSongId = song.id,
                playerName = state.currentPlayerName.orEmpty()
            )
        }
    }
}
