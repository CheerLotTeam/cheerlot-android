package com.gms.cheerlotandroid.data.storage.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gms.cheerlotandroid.data.storage.local.dao.CheerSongDao
import com.gms.cheerlotandroid.data.storage.local.dao.PlayerDao
import com.gms.cheerlotandroid.data.storage.local.dao.TeamDao
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
@TypeConverters(GameScheduleConverter::class)
abstract class CheerLotDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun cheerSongDao(): CheerSongDao

    companion object {
        const val DATABASE_NAME = "cheerlot.db"
        const val VERSION = 1
    }
}
