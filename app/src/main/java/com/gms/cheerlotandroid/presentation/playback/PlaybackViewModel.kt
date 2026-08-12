package com.gms.cheerlotandroid.presentation.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.playback.RepeatMode
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.PlayViewType
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
    private val audioPlayer: AudioPlayer,
    private val analyticsService: AnalyticsService,
) : ViewModel() {

    val uiState: StateFlow<PlaybackUiState> = audioPlayer.state
        .map { state ->
            val song = state.nowPlaying
            val playerName = state.currentPlayerName.orEmpty()

            PlaybackUiState(
                title = song?.title.orEmpty(),
                playerName = playerName,
                lyrics = song?.lyrics.orEmpty(),
                teamInitial = state.teamId?.value?.take(1)
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

    // 화면만 닫고 재생 위치/상태는 그대로 둡니다. 미니플레이어가 이어서 보여줍니다.
    fun close(onClosed: () -> Unit) {
        val state = audioPlayer.state.value
        analyticsService.track(
            AnalyticsEvent.PlayViewDismissed(
                source = state.source,
                viewType = PlayViewType.PLAYBACK,
                isPlaying = state.isPlaying,
                isGameDay = state.isGameDay,
                playerId = state.currentPlayerId,
            )
        )
        onClosed()
    }

    fun trackPresented() {
        val state = audioPlayer.state.value
        analyticsService.track(
            AnalyticsEvent.PlayViewPresented(
                source = state.source,
                viewType = PlayViewType.PLAYBACK,
                isPlaying = state.isPlaying,
                isGameDay = state.isGameDay,
                playerId = state.currentPlayerId,
            )
        )
    }
}
