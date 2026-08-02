package com.gms.cheerlotandroid.core.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.playback.PlaybackState
import com.gms.cheerlotandroid.domain.model.playback.RepeatMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val POSITION_TICK_INTERVAL_MS = 200L
// 이전 곡으로 이동 시, 이 시간(ms) 이상 재생됐으면 이전곡 대신 현재 곡을 처음으로 되감습니다.
private const val REWIND_THRESHOLD_MS = 3_000L

// ExoPlayer 기반 단일 플레이어 구현체입니다.
// iOS AudioPlaybackService/LineupPlaybackService를 하나로 통합한 대응체이며, playbackMode로 정책을 구분합니다.
class AudioPlaybackPlayer(context: Context) : AudioPlayer {

    private val appContext = context.applicationContext

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            /* handleAudioFocus = */ true
        )
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state

    // 현재 재생 큐. originalQueue/originalPlayerNames는 셔플 끄면 복원할 원본 순서를 보관합니다.
    private var queue: List<CheerSongInfo> = emptyList()
    private var queuePlayerNames: List<String> = emptyList()
    private var originalQueue: List<CheerSongInfo> = emptyList()
    private var originalPlayerNames: List<String> = emptyList()
    private var currentIndex: Int = 0

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    handleTrackEnded()
                }
            }
        })
        startPositionTicker()
    }

    override fun play(song: CheerSongInfo, playerName: String?, teamId: TeamId?) {
        // 단일 곡 재생은 iOS AudioPlaybackService.play(_:playerName:coverImageName:)와 동일하게
        // 셔플/반복 상태를 초기화합니다. playQueue()는 반대로 기존 셔플/반복 상태를 그대로 유지합니다.
        _state.update { it.copy(isShuffleEnabled = false, repeatMode = RepeatMode.OFF) }
        playQueue(
            songs = listOf(song),
            playerNames = listOf(playerName.orEmpty()),
            startAt = 0,
            teamId = teamId,
            mode = PlaybackMode.NORMAL
        )
    }

    override fun playQueue(
        songs: List<CheerSongInfo>,
        playerNames: List<String>,
        startAt: Int,
        teamId: TeamId?,
        mode: PlaybackMode
    ) {
        if (songs.isEmpty() || songs.size != playerNames.size || startAt !in songs.indices) return

        queue = songs
        queuePlayerNames = playerNames
        originalQueue = songs
        originalPlayerNames = playerNames
        currentIndex = startAt

        _state.update {
            it.copy(
                playbackMode = mode,
                teamId = teamId
            )
        }

        // 이미 셔플이 켜져 있었다면(iOS AudioPlaybackService.playQueue와 동일하게), 새 큐도 시작 곡을
        // 맨 앞에 고정한 채로 다시 셔플합니다.
        if (_state.value.isShuffleEnabled) {
            applyShuffledQueue(songs[startAt])
        }

        playCurrentSong()
    }

    override fun playAt(index: Int) {
        if (index !in queue.indices || index == currentIndex) return
        currentIndex = index
        playCurrentSong()
    }

    override fun playNext() {
        if (queue.isEmpty()) return

        // 라인업은 iOS LineupPlaybackService와 동일하게 모드 제한 없이 항상 다음곡으로 넘어가며,
        // 마지막 곡이면 처음으로 wrap합니다 (곡이 1개면 같은 곡을 재시작 = 사실상 무한 반복).
        if (_state.value.playbackMode == PlaybackMode.LINEUP) {
            currentIndex = if (currentIndex + 1 < queue.size) currentIndex + 1 else 0
            playCurrentSong()
            return
        }

        if (!_state.value.canSkipManually) return

        currentIndex = (currentIndex + 1) % queue.size
        playCurrentSong()
    }

    override fun playPrevious() {
        if (queue.isEmpty()) return

        // 라인업은 iOS LineupPlaybackService와 동일하게 3초 되감기 없이 바로 이전곡으로 이동하고,
        // 첫 곡에서는 wrap하지 않고 그대로 유지합니다.
        if (_state.value.playbackMode == PlaybackMode.LINEUP) {
            if (currentIndex - 1 < 0) return
            currentIndex -= 1
            playCurrentSong()
            return
        }

        if (!_state.value.canSkipManually) return

        if (exoPlayer.currentPosition > REWIND_THRESHOLD_MS) {
            seek(0)
            return
        }

        currentIndex = (currentIndex - 1 + queue.size) % queue.size
        playCurrentSong()
    }

    override fun setShuffleEnabled(isEnabled: Boolean) {
        if (_state.value.isShuffleEnabled == isEnabled) return

        val currentSong = _state.value.nowPlaying
        if (currentSong == null) {
            _state.update { it.copy(isShuffleEnabled = isEnabled) }
            return
        }

        if (isEnabled) {
            applyShuffledQueue(currentSong)
        } else {
            restoreOriginalQueue(currentSong)
        }

        _state.update {
            it.copy(
                isShuffleEnabled = isEnabled,
                currentPlayerName = queuePlayerNames.getOrNull(currentIndex),
                currentQueueIndex = currentIndex
            )
        }
    }

    override fun setRepeatMode(mode: RepeatMode) {
        _state.update { it.copy(repeatMode = mode) }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        exoPlayer.play()
    }

    override fun toggle() {
        if (_state.value.isPlaying) pause() else resume()
    }

    override fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        queue = emptyList()
        queuePlayerNames = emptyList()
        originalQueue = emptyList()
        originalPlayerNames = emptyList()
        currentIndex = 0

        _state.update {
            PlaybackState()
        }
    }

    override fun seek(positionMs: Long) {
        val duration = _state.value.durationMs
        val target = positionMs.coerceIn(0L, if (duration > 0) duration else Long.MAX_VALUE)
        exoPlayer.seekTo(target)
        updatePositionState()
    }

    override fun resetToBeginning() {
        exoPlayer.seekTo(0)
        updatePositionState()
    }

    private fun playCurrentSong() {
        val song = queue.getOrNull(currentIndex) ?: return
        val playerName = queuePlayerNames.getOrNull(currentIndex)
        val mediaItem = mediaItemFor(song) ?: return

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _state.update {
            it.copy(
                nowPlaying = song,
                currentPlayerName = playerName,
                currentQueueIndex = currentIndex,
                queueSize = queue.size,
                currentPositionMs = 0L
            )
        }
    }

    private fun handleTrackEnded() {
        if (queue.isEmpty()) return

        val state = _state.value

        // 라인업은 셔플/반복 개념이 없고, iOS LineupPlaybackService처럼 곡이 끝나면 항상 다음곡으로
        // 진행합니다(마지막 곡+큐 1개면 같은 곡을 재시작 = 무한 반복, 그 외엔 다음곡/처음으로 wrap).
        if (state.playbackMode == PlaybackMode.LINEUP) {
            currentIndex = if (currentIndex + 1 < queue.size) currentIndex + 1 else 0
            playCurrentSong()
            return
        }

        if (state.repeatMode == RepeatMode.ONE || state.playbackMode == PlaybackMode.SEARCH) {
            exoPlayer.seekTo(0)
            exoPlayer.play()
            return
        }

        if (currentIndex + 1 < queue.size) {
            currentIndex += 1
            playCurrentSong()
            return
        }

        if (queue.size <= 1) return

        currentIndex = 0
        playCurrentSong()
    }

    private fun applyShuffledQueue(currentSong: CheerSongInfo) {
        val sourceSongs = originalQueue.ifEmpty { queue }
        val sourceNames = originalPlayerNames.ifEmpty { queuePlayerNames }
        val pairs = sourceSongs.zip(sourceNames)
        val currentPair = pairs.firstOrNull { it.first.id == currentSong.id } ?: return

        val shuffledRest = pairs.filter { it.first.id != currentSong.id }.shuffled()

        queue = listOf(currentPair.first) + shuffledRest.map { it.first }
        queuePlayerNames = listOf(currentPair.second) + shuffledRest.map { it.second }
        currentIndex = 0
    }

    private fun restoreOriginalQueue(currentSong: CheerSongInfo) {
        if (originalQueue.isEmpty()) return

        queue = originalQueue
        queuePlayerNames = originalPlayerNames
        currentIndex = originalQueue.indexOfFirst { it.id == currentSong.id }.takeIf { it >= 0 } ?: 0
    }

    // 로컬 리소스를 찾지 못하면 null을 반환합니다 (iOS playBundle의 assertionFailure + 조기 반환과 동일한 처리).
    private fun mediaItemFor(song: CheerSongInfo): MediaItem? {
        val audioUrl = song.audioUrl
        val uri = if (audioUrl.startsWith("http")) {
            Uri.parse(audioUrl)
        } else {
            val name = audioUrl.substringBeforeLast('.', audioUrl)
            val resId = appContext.resources.getIdentifier(name, "raw", appContext.packageName)
            if (resId == 0) return null
            Uri.parse("android.resource://${appContext.packageName}/$resId")
        }
        return MediaItem.fromUri(uri)
    }

    private fun startPositionTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (isActive) {
                updatePositionState()
                delay(POSITION_TICK_INTERVAL_MS)
            }
        }
    }

    private fun updatePositionState() {
        val duration = exoPlayer.duration.takeIf { it != C.TIME_UNSET } ?: 0L
        _state.update {
            it.copy(
                currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
                durationMs = duration.coerceAtLeast(0L)
            )
        }
    }
}
