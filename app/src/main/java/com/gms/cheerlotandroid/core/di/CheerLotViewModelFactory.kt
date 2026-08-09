package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gms.cheerlotandroid.presentation.appflow.AppFlowViewModel
import com.gms.cheerlotandroid.presentation.lineup.LineupViewModel
import com.gms.cheerlotandroid.presentation.lineupchange.LineupChangeViewModel
import com.gms.cheerlotandroid.presentation.lineupplayback.LineupPlaybackViewModel
import com.gms.cheerlotandroid.presentation.main.MainViewModel
import com.gms.cheerlotandroid.presentation.main.MiniPlayerViewModel
import com.gms.cheerlotandroid.presentation.playback.PlaybackViewModel
import com.gms.cheerlotandroid.presentation.search.SearchViewModel
import com.gms.cheerlotandroid.presentation.settings.SettingsViewModel
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersViewModel

// 화면별 ViewModel 생성 규칙을 한 곳에 모아두는 Factory입니다.
// 인스턴스 자체는 보관하지 않고 생성 방법만 제공하며, 생명주기는 ViewModelStore가 관리합니다.
//
// 화면 진입 시점 값(nav args 등)이 생성자에 필요한 ViewModel은 이곳에 분기 처리하지 않습니다.
// 이 Factory는 modelClass 하나로만 분기하므로 호출마다 다른 인자를 줄 수 없습니다.
// 그런 경우 해당 화면(Composable)에서 viewModelFactory { initializer { ... } }로 직접 생성합니다.
class CheerLotViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppFlowViewModel::class.java) -> {
                AppFlowViewModel(
                    hasSelectedTeamUseCase = appContainer.hasSelectedTeamUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(MiniPlayerViewModel::class.java) -> {
                MiniPlayerViewModel(
                    audioPlayer = appContainer.audioPlayer,
                ) as T
            }

            modelClass.isAssignableFrom(PlaybackViewModel::class.java) -> {
                PlaybackViewModel(
                    audioPlayer = appContainer.audioPlayer,
                ) as T
            }

            modelClass.isAssignableFrom(LineupViewModel::class.java) -> {
                LineupViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    getAllPlayersUseCase = appContainer.getAllPlayersUseCase,
                    getLineupUseCase = appContainer.getLineupUseCase,
                    getLineupGameInfoUseCase = appContainer.getLineupGameInfoUseCase,
                    getTeamGameScheduleUseCase = appContainer.getTeamGameScheduleUseCase,
                    getTeamUseCase = appContainer.getTeamUseCase,
                    playLineupSongsUseCase = appContainer.playLineupSongsUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(LineupPlaybackViewModel::class.java) -> {
                LineupPlaybackViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    getLineupUseCase = appContainer.getLineupUseCase,
                    getLineupGameInfoUseCase = appContainer.getLineupGameInfoUseCase,
                    getTeamUseCase = appContainer.getTeamUseCase,
                    audioPlayer = appContainer.audioPlayer,
                ) as T
            }

            modelClass.isAssignableFrom(LineupChangeViewModel::class.java) -> {
                LineupChangeViewModel(
                    getBenchPlayersUseCase = appContainer.getBenchPlayersUseCase,
                    swapLineupPlayersUseCase = appContainer.swapLineupPlayersUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(TeamMembersViewModel::class.java) -> {
                TeamMembersViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    getAllPlayersUseCase = appContainer.getAllPlayersUseCase,
                    playTeamMembersUseCase = appContainer.playTeamMembersUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    getAllPlayersUseCase = appContainer.getAllPlayersUseCase,
                    playSearchResultUseCase = appContainer.playSearchResultUseCase,
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                ) as T
            }

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}. " +
                    "Add its creation logic to CheerLotViewModelFactory."
            )
        }
    }
}
