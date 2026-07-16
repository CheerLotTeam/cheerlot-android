package com.gms.cheerlotandroid.data.storage.local

import androidx.room.TypeConverter
import com.gms.cheerlotandroid.data.storage.local.entity.TeamRecentGameLocalDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TeamEntity.recentGames는 Kotlin 코드에서는 객체 리스트로 다루되,
// Room이 실제로는 이 컨버터를 통해 하나의 TEXT 컬럼(JSON)으로 저장합니다.
class GameScheduleConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromRecentGames(value: List<TeamRecentGameLocalDto>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toRecentGames(value: String): List<TeamRecentGameLocalDto> {
        if (value.isBlank()) return emptyList()
        return runCatching {
            json.decodeFromString<List<TeamRecentGameLocalDto>>(value)
        }.getOrDefault(emptyList())
    }
}
