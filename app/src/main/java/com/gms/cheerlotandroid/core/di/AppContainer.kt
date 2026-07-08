package com.gms.cheerlotandroid.core.di

import android.content.Context
import androidx.room.Room
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabase
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabaseMigrations

// 앱 전역 의존성은 AppContainer에서 한 번 생성하고 필요한 계층으로 전달합니다.
class AppContainer(
    context: Context,
) {
    private val appContext = context.applicationContext

    // Room Database는 앱 전체에서 하나의 인스턴스를 공유합니다.
    val database: CheerLotDatabase by lazy {
        Room.databaseBuilder(
            context = appContext,
            klass = CheerLotDatabase::class.java,
            name = CheerLotDatabase.DATABASE_NAME,
        )
            .addMigrations(*CheerLotDatabaseMigrations.all)
            .build()
    }

    // ViewModel 인스턴스는 Android ViewModelStore가 관리하고, Factory는 생성 방법만 제공합니다.
    val viewModelFactory: CheerLotViewModelFactory by lazy {
        CheerLotViewModelFactory(this)
    }
}

/*
새 의존성을 추가하는 예시:

1. RepositoryImpl이 필요한 경우

val teamRepository: TeamRepository by lazy {
    TeamRepositoryImpl(
        database = database,
        teamDataSource = TeamDataSource,
    )
}

2. UseCase가 필요한 경우

val getSelectedTeamUseCase: GetSelectedTeamUseCase by lazy {
    GetSelectedTeamUseCaseImpl(
        teamRepository = teamRepository,
    )
}

3. Service가 필요한 경우

val playbackService: PlaybackService by lazy {
    PlaybackServiceImpl(
        context = appContext,
    )
}

원칙:
- DataSource, RepositoryImpl, UseCase, Service 생성 위치는 AppContainer로 모읍니다.
- ViewModel이나 Composable에서 RepositoryImpl, Database, Service를 직접 생성하지 않습니다.
- 실제 ViewModel 생성은 viewModelFactory에서 처리합니다.
*/
