package com.gms.cheerlotandroid.domain.service.analytics

enum class AppEntryPoint(val value: String) { APP("app"), WIDGET("widget"), PUSH("push") }
enum class PlaySource(val value: String) { LINEUP("lineup"), TEAM_MEMBERS("teamMembers"), SEARCH("search") }
enum class PlayViewType(val value: String) { LINEUP_PLAYBACK("lineup_playback"), PLAYBACK("playback") }
enum class PlayTrigger(val value: String) { USER_TAP("user_tap"), AUTO_NEXT("auto_next") }
enum class AnalyticsUserProperty(val value: String) { TEAM_ID("team_id"), TOTAL_PLAY_COUNT("total_play_count") }

sealed interface AnalyticsEvent {
    val name: String
    val properties: Map<String, Any>

    data class AppOpen(
        val entryPoint: AppEntryPoint,
        val widgetId: String? = null,
        val isGameDay: Boolean,
    ) : AnalyticsEvent {
        override val name = "app_open"
        override val properties = buildMap {
            put("entry_point", entryPoint.value)
            put("is_game_day", isGameDay)
            widgetId?.let { put("widget_id", it) }
        }
    }

    data class PlayViewPresented(
        val source: PlaySource,
        val viewType: PlayViewType,
        val isPlaying: Boolean,
        val isGameDay: Boolean,
        val playerId: String,
    ) : AnalyticsEvent {
        override val name = "play_view_presented"
        override val properties = playbackProperties(source, viewType, isPlaying, isGameDay, playerId)
    }

    data class PlayViewDismissed(
        val source: PlaySource,
        val viewType: PlayViewType,
        val isPlaying: Boolean,
        val isGameDay: Boolean,
        val playerId: String,
    ) : AnalyticsEvent {
        override val name = "play_view_dismissed"
        override val properties = playbackProperties(source, viewType, isPlaying, isGameDay, playerId)
    }

    data class CheerPlayStarted(
        val source: PlaySource,
        val trigger: PlayTrigger,
        val isGameDay: Boolean,
        val playerId: String,
    ) : AnalyticsEvent {
        override val name = "cheer_play_started"
        override val properties = mapOf(
            "source" to source.value,
            "trigger" to trigger.value,
            "is_game_day" to isGameDay,
            "player_id" to playerId,
        )
    }
}

private fun playbackProperties(
    source: PlaySource,
    viewType: PlayViewType,
    isPlaying: Boolean,
    isGameDay: Boolean,
    playerId: String,
) = mapOf(
    "source" to source.value,
    "view_type" to viewType.value,
    "is_playing" to isPlaying,
    "is_game_day" to isGameDay,
    "player_id" to playerId,
)
