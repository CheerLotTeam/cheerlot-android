package com.gms.cheerlotandroid.presentation.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.design.team.TeamAsset
import com.gms.cheerlotandroid.domain.model.playback.RepeatMode
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal data class PlaybackUiState(
    val title: String = "",
    val playerName: String = "",
    val lyrics: String = "",
    val teamInitial: String = "",
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false,
    val canSkipManually: Boolean = false
)

// CheerLotFullScreen.BasePlayback(전체선수/검색 재생) 화면의 상태/액션.
// 큐는 이미 화면 진입 전에 재생이 시작돼 있으므로, 별도 nav 인자 없이 audioPlayer.state만 관찰합니다.
internal class PlaybackViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val uiState: StateFlow<PlaybackUiState> = audioPlayer.state
        .map { state ->
            val song = state.nowPlaying
            val playerName = state.currentPlayerName.orEmpty()

            PlaybackUiState(
                title = song?.title.orEmpty(),
                playerName = playerName,
                lyrics = song?.lyrics.orEmpty(),
                teamInitial = state.teamId?.let { TeamAsset.from(it).assetPrefix.uppercase() }
                    ?: playerName.take(1),
                isPlaying = state.isPlaying,
                progressMs = state.currentPositionMs,
                durationMs = state.durationMs,
                isShuffleEnabled = state.isShuffleEnabled,
                isRepeatOneEnabled = state.repeatMode == RepeatMode.ONE,
                canSkipManually = state.canSkipManually
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaybackUiState()
        )

    fun togglePlayback() {
        audioPlayer.toggle()
    }

    fun seek(positionMs: Long) {
        audioPlayer.seek(positionMs)
    }

    fun playNext() {
        audioPlayer.playNext()
    }

    fun playPrevious() {
        audioPlayer.playPrevious()
    }

    fun toggleShuffle() {
        audioPlayer.setShuffleEnabled(!uiState.value.isShuffleEnabled)
    }

    fun toggleRepeatOne() {
        val nextMode = if (uiState.value.isRepeatOneEnabled) RepeatMode.OFF else RepeatMode.ONE
        audioPlayer.setRepeatMode(nextMode)
    }

    // 화면을 닫을 때 현재 곡을 처음으로 되돌립니다(재생 자체는 멈추지 않음). iOS PlaybackViewModel과 동일한 동작입니다.
    fun close(onClosed: () -> Unit) {
        audioPlayer.resetToBeginning()
        onClosed()
    }
}
