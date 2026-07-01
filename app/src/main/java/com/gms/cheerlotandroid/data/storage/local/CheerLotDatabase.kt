package com.gms.cheerlotandroid.data.storage.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gms.cheerlotandroid.data.storage.local.entity.CheerSongEntity
import com.gms.cheerlotandroid.data.storage.local.entity.PlayerEntity
import com.gms.cheerlotandroid.data.storage.local.entity.TeamEntity

@Database(
    entities = [
        TeamEntity::class,
        PlayerEntity::class,
        CheerSongEntity::class
    ],
    version = CheerLotDatabase.VERSION,
    exportSchema = true
)
abstract class CheerLotDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "cheerlot.db"
        const val VERSION = 1
    }
}
