package com.gms.cheerlotandroid.core.di

import android.content.Context
import androidx.room.Room
import com.gms.cheerlotandroid.data.network.NetworkModule
import com.gms.cheerlotandroid.data.repository.PlayerRepositoryImpl
import com.gms.cheerlotandroid.data.repository.TeamRepositoryImpl
import com.gms.cheerlotandroid.data.repository.TeamSelectionRepositoryImpl
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.data.storage.datastore.teamSelectionDataStore
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabase
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabaseMigrations
import com.gms.cheerlotandroid.core.media.AudioPlaybackPlayer
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import com.gms.cheerlotandroid.domain.repository.TeamRepository
import com.gms.cheerlotandroid.domain.repository.TeamSelectionRepository
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.playback.PlayLineupSongsUseCase
import com.gms.cheerlotandroid.domain.usecase.player.GetAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.player.GetPlayerDetailUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetAllTeamsUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamGameScheduleUseCase
import com.gms.cheerlotandroid.domain.usecase.team.HasSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.UpdateSelectedTeamUseCase

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

    private val networkModule: NetworkModule by lazy { NetworkModule() }

    // 앱 전역에서 하나의 ExoPlayer 인스턴스를 공유합니다.
    val audioPlayer: AudioPlayer by lazy {
        AudioPlaybackPlayer(context = appContext)
    }

    val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(
            teamDao = database.teamDao(),
            teamApiService = networkModule.teamApiService,
            teamCatalog = TeamCatalog,
        )
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl(
            database = database,
            playerDao = database.playerDao(),
            cheerSongDao = database.cheerSongDao(),
            playerApiService = networkModule.playerApiService,
            teamRepository = teamRepository,
            teamCatalog = TeamCatalog,
        )
    }

    val teamSelectionRepository: TeamSelectionRepository by lazy {
        TeamSelectionRepositoryImpl(
            dataStore = appContext.teamSelectionDataStore,
        )
    }

    val getLineupUseCase: GetLineupUseCase by lazy {
        GetLineupUseCase(playerRepository = playerRepository)
    }

    val getAllPlayersUseCase: GetAllPlayersUseCase by lazy {
        GetAllPlayersUseCase(playerRepository = playerRepository)
    }

    val getPlayerDetailUseCase: GetPlayerDetailUseCase by lazy {
        GetPlayerDetailUseCase(playerRepository = playerRepository)
    }

    val getLineupGameInfoUseCase: GetLineupGameInfoUseCase by lazy {
        GetLineupGameInfoUseCase(teamRepository = teamRepository)
    }

    val getTeamGameScheduleUseCase: GetTeamGameScheduleUseCase by lazy {
        GetTeamGameScheduleUseCase(teamRepository = teamRepository)
    }

    val getAllTeamsUseCase: GetAllTeamsUseCase by lazy {
        GetAllTeamsUseCase(teamRepository = teamRepository)
    }

    val getSelectedTeamUseCase: GetSelectedTeamUseCase by lazy {
        GetSelectedTeamUseCase(teamSelectionRepository = teamSelectionRepository)
    }

    val updateSelectedTeamUseCase: UpdateSelectedTeamUseCase by lazy {
        UpdateSelectedTeamUseCase(teamSelectionRepository = teamSelectionRepository)
    }

    val hasSelectedTeamUseCase: HasSelectedTeamUseCase by lazy {
        HasSelectedTeamUseCase(teamSelectionRepository = teamSelectionRepository)
    }

    val playLineupSongsUseCase: PlayLineupSongsUseCase by lazy {
        PlayLineupSongsUseCase(audioPlayer = audioPlayer)
    }

    // ViewModel 인스턴스는 Android ViewModelStore가 관리하고, Factory는 생성 방법만 제공합니다.
    val viewModelFactory: CheerLotViewModelFactory by lazy {
        CheerLotViewModelFactory(this)
    }
}
