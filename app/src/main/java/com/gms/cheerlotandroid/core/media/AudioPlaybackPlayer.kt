package com.gms.cheerlotandroid.core.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gms.cheerlotandroid.design.color.team.TeamColor
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.playback.PlaybackState
import com.gms.cheerlotandroid.domain.model.playback.RepeatMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsUserProperty
import com.gms.cheerlotandroid.domain.service.analytics.PlaySource
import com.gms.cheerlotandroid.domain.service.analytics.PlayTrigger
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
class AudioPlaybackPlayer(
    context: Context,
    private val analyticsService: AnalyticsService,
) : AudioPlayer {

    private val appContext = context.applicationContext

    // MediaSessionService(CheerLotPlaybackService)가 같은 인스턴스에 MediaSession을 붙여서
    // 백그라운드 재생/알림/잠금화면 컨트롤을 제공합니다. AudioPlayer(domain) 인터페이스에는
    // 플랫폼 타입을 노출하지 않기 위해, 이 구체 클래스에서만 internal로 열어둡니다.
    internal val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
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
    private var queuePlayerIds: List<String> = emptyList()
    private var originalQueue: List<CheerSongInfo> = emptyList()
    private var originalPlayerNames: List<String> = emptyList()
    private var originalPlayerIds: List<String> = emptyList()
    private var currentIndex: Int = 0
    private var pendingTrigger = PlayTrigger.USER_TAP

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
        mode: PlaybackMode,
        playerIds: List<String>,
        isGameDay: Boolean,
    ) {
        if (songs.isEmpty() || songs.size != playerNames.size || startAt !in songs.indices) return

        // 라인업 재생은 Shorts 방식(화면을 나가면 정지)이라 백그라운드로 이어지면 안 됩니다.
        // 이전에 NORMAL/SEARCH 재생으로 떠 있던 서비스가 있다면 여기서 확실히 내립니다.
        if (mode == PlaybackMode.LINEUP) {
            stopPlaybackService()
        } else {
            startPlaybackService()
        }

        queue = songs
        queuePlayerNames = playerNames
        queuePlayerIds = List(songs.size) { index -> playerIds.getOrNull(index).orEmpty() }
        originalQueue = songs
        originalPlayerNames = playerNames
        originalPlayerIds = queuePlayerIds
        currentIndex = startAt
        pendingTrigger = PlayTrigger.USER_TAP

        _state.update {
            it.copy(
                playbackMode = mode,
                teamId = teamId,
                source = when (mode) {
                    PlaybackMode.LINEUP -> PlaySource.LINEUP
                    PlaybackMode.SEARCH -> PlaySource.SEARCH
                    PlaybackMode.NORMAL -> PlaySource.TEAM_MEMBERS
                },
                isGameDay = isGameDay,
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
        pendingTrigger = PlayTrigger.USER_TAP
        playCurrentSong()
    }

    override fun playNext() {
        if (queue.isEmpty()) return

        // 라인업은 iOS LineupPlaybackService와 동일하게 모드 제한 없이 항상 다음곡으로 넘어가며,
        // 마지막 곡이면 처음으로 wrap합니다 (곡이 1개면 같은 곡을 재시작 = 사실상 무한 반복).
        if (_state.value.playbackMode == PlaybackMode.LINEUP) {
            currentIndex = if (currentIndex + 1 < queue.size) currentIndex + 1 else 0
            pendingTrigger = PlayTrigger.USER_TAP
            playCurrentSong()
            return
        }

        if (!_state.value.canSkipManually) return

        currentIndex = (currentIndex + 1) % queue.size
        pendingTrigger = PlayTrigger.USER_TAP
        playCurrentSong()
    }

    override fun playPrevious() {
        if (queue.isEmpty()) return

        // 라인업은 iOS LineupPlaybackService와 동일하게 3초 되감기 없이 바로 이전곡으로 이동하고,
        // 첫 곡에서는 wrap하지 않고 그대로 유지합니다.
        if (_state.value.playbackMode == PlaybackMode.LINEUP) {
            if (currentIndex - 1 < 0) return
            currentIndex -= 1
            pendingTrigger = PlayTrigger.USER_TAP
            playCurrentSong()
            return
        }

        if (!_state.value.canSkipManually) return

        if (exoPlayer.currentPosition > REWIND_THRESHOLD_MS) {
            seek(0)
            return
        }

        currentIndex = (currentIndex - 1 + queue.size) % queue.size
        pendingTrigger = PlayTrigger.USER_TAP
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
        queuePlayerIds = emptyList()
        originalQueue = emptyList()
        originalPlayerNames = emptyList()
        originalPlayerIds = emptyList()
        currentIndex = 0

        _state.update {
            PlaybackState()
        }

        stopPlaybackService()
    }

    override fun seek(positionMs: Long) {
        val duration = _state.value.durationMs
        val target = positionMs.coerceIn(0L, if (duration > 0) duration else Long.MAX_VALUE)
        exoPlayer.seekTo(target)
        updatePositionState()
    }

    private fun playCurrentSong() {
        val song = queue.getOrNull(currentIndex) ?: return
        val playerName = queuePlayerNames.getOrNull(currentIndex)
        val playerId = queuePlayerIds.getOrNull(currentIndex).orEmpty()
        val mediaItem = mediaItemFor(song, playerName, _state.value.teamId) ?: return

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _state.update {
            it.copy(
                nowPlaying = song,
                currentPlayerName = playerName,
                currentPlayerId = playerId,
                currentQueueIndex = currentIndex,
                queueSize = queue.size,
                currentPositionMs = 0L
            )
        }
        // 클릭 시점이 아니라 MediaItem이 실제로 재생기에 올라간 시점만 재생 시작으로 집계합니다.
        analyticsService.track(
            AnalyticsEvent.CheerPlayStarted(
                source = _state.value.source,
                trigger = pendingTrigger,
                isGameDay = _state.value.isGameDay,
                playerId = playerId,
            )
        )
        analyticsService.incrementUserProperty(AnalyticsUserProperty.TOTAL_PLAY_COUNT)
        pendingTrigger = PlayTrigger.USER_TAP
    }

    private fun handleTrackEnded() {
        if (queue.isEmpty()) return

        val state = _state.value

        // 라인업은 셔플/반복 개념이 없고, iOS LineupPlaybackService처럼 곡이 끝나면 항상 다음곡으로
        // 진행합니다(마지막 곡+큐 1개면 같은 곡을 재시작 = 무한 반복, 그 외엔 다음곡/처음으로 wrap).
        if (state.playbackMode == PlaybackMode.LINEUP) {
            currentIndex = if (currentIndex + 1 < queue.size) currentIndex + 1 else 0
            // 곡 종료에 의한 이동만 auto_next이며, 버튼·Pager 이동은 user_tap으로 기록합니다.
            pendingTrigger = PlayTrigger.AUTO_NEXT
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
            pendingTrigger = PlayTrigger.AUTO_NEXT
            playCurrentSong()
            return
        }

        if (queue.size <= 1) return

        currentIndex = 0
        pendingTrigger = PlayTrigger.AUTO_NEXT
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
        queuePlayerIds = queue.map { song ->
            originalQueue.indexOfFirst { it.id == song.id }
                .takeIf { it >= 0 }
                ?.let { index -> originalPlayerIds.getOrNull(index) }
                .orEmpty()
        }
        currentIndex = 0
    }

    private fun restoreOriginalQueue(currentSong: CheerSongInfo) {
        if (originalQueue.isEmpty()) return

        queue = originalQueue
        queuePlayerNames = originalPlayerNames
        queuePlayerIds = originalPlayerIds
        currentIndex = originalQueue.indexOfFirst { it.id == currentSong.id }.takeIf { it >= 0 } ?: 0
    }

    // CheerLotPlaybackService(MediaSessionService)를 살려서 백그라운드 재생/알림/잠금화면 컨트롤을 켭니다.
    // 이미 떠 있으면 onStartCommand만 다시 불릴 뿐 재생성되지 않으므로, 매 playQueue()마다 불러도 안전합니다.
    private fun startPlaybackService() {
        ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, CheerLotPlaybackService::class.java)
        )
    }

    private fun stopPlaybackService() {
        appContext.stopService(Intent(appContext, CheerLotPlaybackService::class.java))
    }

    // 로컬 리소스를 찾지 못하면 null을 반환합니다 (iOS playBundle의 assertionFailure + 조기 반환과 동일한 처리).
    // title/artist를 직접 채워서, MediaSession 알림이 음원 파일 자체에 박힌 메타데이터
    // (원본 유튜브 영상 제목 등) 대신 우리가 가진 선수명/응원가 제목을 보여주게 합니다.
    private fun mediaItemFor(song: CheerSongInfo, playerName: String?, teamId: TeamId?): MediaItem? {
        val audioUrl = song.audioUrl
        val uri = if (audioUrl.startsWith("http")) {
            Uri.parse(audioUrl)
        } else {
            val name = audioUrl.substringBeforeLast('.', audioUrl)
            val resId = appContext.resources.getIdentifier(name, "raw", appContext.packageName)
            if (resId == 0) return null
            Uri.parse("android.resource://${appContext.packageName}/$resId")
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(playerName)
            .setArtworkUri(teamId?.let { coverArtUriFor(it) })
            .build()

        return MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(metadata)
            .build()
    }

    // iOS TeamAssetVO.coverImageName("{assetPrefix}_cover")과 동일한 팀별 커버 이미지를 사용합니다.
    // 알림/잠금화면도 594x594 원본을 그대로 쓰면 시스템 UI가 훨씬 작은 크기로 축소하면서 디더링
    // 노이즈가 생겨서(MiniPlayer와 동일한 문제), 미리 축소해둔 256x256 썸네일을 씁니다.
    private fun coverArtUriFor(teamId: TeamId): Uri? {
        val resId = TeamColor.coverThumbnailRes(teamId) ?: return null
        return Uri.parse("android.resource://${appContext.packageName}/$resId")
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
