package com.gms.cheerlotandroid.core.media

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer

// iOS AudioPlaybackService.setupRemoteCommands()와 동일한 역할을 합니다.
// 큐(다음곡/이전곡/셔플/반복)는 ExoPlayer가 아니라 AudioPlaybackPlayer가 직접 관리하고,
// ExoPlayer에는 매번 곡을 하나씩만 올립니다(setMediaItem). 그래서 시스템 알림/잠금화면에
// 원본 ExoPlayer를 그대로 노출하면 "다음곡" 액션 자체가 안 뜨고(hasNextMediaItem이 항상
// false), "이전곡"도 진짜 이전곡 로직이 아니라 그냥 현재 곡 재시작으로만 동작합니다.
//
// MediaSession에 원본 ExoPlayer 대신 이 래퍼를 넘겨서
// 1) getAvailableCommands()에서 SEEK_TO_NEXT/PREVIOUS를 항상 사용 가능하다고 알리고
// 2) seekToNext()/seekToPrevious() 호출 자체를 가로채 audioPlayer.playNext()/playPrevious()
//    (앱의 진짜 큐 로직)로 라우팅합니다. ExoPlayer 자체의 seek 동작은 실행하지 않습니다.
class SkipRoutingPlayer(
    exoPlayer: ExoPlayer,
    private val audioPlayer: AudioPlayer
) : ForwardingPlayer(exoPlayer) {

    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands().buildUpon()
            .add(Player.COMMAND_SEEK_TO_NEXT)
            .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            .add(Player.COMMAND_SEEK_TO_PREVIOUS)
            .add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
            .build()
    }

    override fun seekToNext() {
        audioPlayer.playNext()
    }

    override fun seekToNextMediaItem() {
        audioPlayer.playNext()
    }

    override fun seekToPrevious() {
        audioPlayer.playPrevious()
    }

    override fun seekToPreviousMediaItem() {
        audioPlayer.playPrevious()
    }
}
