package com.gms.cheerlotandroid.data.storage.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cheer_songs",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["player_id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_id"])
    ]
)
data class CheerSongEntity(
    @PrimaryKey
    @ColumnInfo(name = "cheer_song_id")
    val cheerSongId: String,
    @ColumnInfo(name = "player_id")
    val playerId: String,
    val title: String,
    val lyrics: String,
    @ColumnInfo(name = "audio_url")
    val audioUrl: String
) {
    companion object {
        fun create(
            playerId: String,
            title: String,
            lyrics: String,
            audioUrl: String
        ): CheerSongEntity {
            return CheerSongEntity(
                cheerSongId = "${playerId}_$title",
                playerId = playerId,
                title = title,
                lyrics = lyrics,
                audioUrl = audioUrl
            )
        }
    }
}
