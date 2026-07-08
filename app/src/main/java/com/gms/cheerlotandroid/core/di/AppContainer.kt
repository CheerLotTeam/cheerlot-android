package com.gms.cheerlotandroid.core.di

import android.content.Context
import androidx.room.Room
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabase
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabaseMigrations

class AppContainer(
    context: Context,
) {
    private val appContext = context.applicationContext

    val database: CheerLotDatabase by lazy {
        Room.databaseBuilder(
            context = appContext,
            klass = CheerLotDatabase::class.java,
            name = CheerLotDatabase.DATABASE_NAME,
        )
            .addMigrations(*CheerLotDatabaseMigrations.all)
            .build()
    }

    val viewModelFactory: CheerLotViewModelFactory by lazy {
        CheerLotViewModelFactory(this)
    }
}
