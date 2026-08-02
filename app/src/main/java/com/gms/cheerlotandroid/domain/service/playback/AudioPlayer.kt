package com.gms.cheerlotandroid.domain.service.playback

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.playback.PlaybackState
import com.gms.cheerlotandroid.domain.model.playback.RepeatMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import kotlinx.coroutines.flow.StateFlow

// 앱 전역 오디오 재생을 담당하는 단일 플레이어 인터페이스입니다.
// iOS의 AudioPlaybackService/LineupPlaybackService를 하나로 통합한 대응체이며, playbackMode로 재생 정책을 구분합니다.
interface AudioPlayer {
    val state: StateFlow<PlaybackState>

    // 단일 곡 재생. 셔플/반복 상태를 초기화합니다.
    fun play(song: CheerSongInfo, playerName: String?, teamId: TeamId?)

    // 곡 목록을 큐로 올려 재생합니다. 이미 셔플이 켜져 있었다면 그 상태를 유지한 채 새 큐도 다시 셔플합니다.
    fun playQueue(
        songs: List<CheerSongInfo>,
        playerNames: List<String>,
        startAt: Int = 0,
        teamId: TeamId?,
        mode: PlaybackMode
    )

    // 큐 안의 임의 인덱스로 바로 이동합니다. 라인업 재생 화면의 카드 스와이프처럼, 한 번에 여러 곡을
    // 건너뛸 수 있는 경우에 씁니다. 범위를 벗어나거나 이미 재생 중인 인덱스면 아무 동작도 하지 않습니다.
    fun playAt(index: Int)

    // 다음곡. 모드별 스킵/wrap 정책은 구현체(AudioPlaybackPlayer)를 따릅니다.
    fun playNext()

    // 이전곡. 모드별 되감기/wrap 정책은 구현체(AudioPlaybackPlayer)를 따릅니다.
    fun playPrevious()

    fun setShuffleEnabled(isEnabled: Boolean)

    fun setRepeatMode(mode: RepeatMode)

    fun pause()

    fun resume()

    fun toggle()

    // 재생을 완전히 멈추고 큐/상태를 초기화합니다.
    fun stop()

    fun seek(positionMs: Long)

    // 재생은 유지한 채 현재 곡만 처음으로 되돌립니다 (재생 화면을 닫을 때 사용).
    fun resetToBeginning()
}
