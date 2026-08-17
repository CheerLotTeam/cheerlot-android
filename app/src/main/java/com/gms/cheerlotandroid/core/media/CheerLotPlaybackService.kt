package com.gms.cheerlotandroid.core.media

import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.gms.cheerlotandroid.app.CheerLotApplication

// 앱 전역 ExoPlayer(AppContainer.audioPlayer.exoPlayer)에 MediaSession을 붙여서
// 백그라운드 재생, 알림, 잠금화면 컨트롤을 제공하는 foreground service입니다.
// 플레이어 자체의 생명주기는 AppContainer가 앱 프로세스 동안 계속 소유하므로,
// 이 서비스는 그 위에 세션만 얹었다가 onDestroy에서 세션만 release합니다(플레이어는 release하지 않음).
class CheerLotPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val audioPlayer = (application as CheerLotApplication).appContainer.audioPlayer
        val sessionPlayer = SkipRoutingPlayer(exoPlayer = audioPlayer.exoPlayer, audioPlayer = audioPlayer)
        val session = MediaSession.Builder(this, sessionPlayer).build()
        mediaSession = session
        // MediaSessionService의 자동 알림/포그라운드 승격은 addSession으로 등록된 세션만 관찰합니다.
        // 이걸 빠뜨리면 startForegroundService() 이후 startForeground()가 제때 호출되지 않아
        // ForegroundServiceDidNotStartInTimeException으로 프로세스 전체가 죽습니다.
        addSession(session)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // 앱이 태스크에서 스와이프로 제거돼도, 재생 중이면 백그라운드 재생을 이어갑니다.
    // 재생 중이 아니면(일시정지 등) 굳이 서비스를 살려둘 이유가 없어 바로 종료합니다.
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.let { session ->
            removeSession(session)
            session.release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
