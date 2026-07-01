package com.gms.cheerlotandroid.data.storage.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["team_id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["team_id"])
    ]
)
data class PlayerEntity(
    @PrimaryKey
    @ColumnInfo(name = "player_id")
    val playerId: String,
    @ColumnInfo(name = "team_id")
    val teamId: String,
    val name: String,
    @ColumnInfo(name = "back_number")
    val backNumber: Int,
    val position: String,
    @ColumnInfo(name = "bat_throw")
    val batThrow: String,
    @ColumnInfo(name = "batting_order")
    val battingOrder: Int? = null
)
