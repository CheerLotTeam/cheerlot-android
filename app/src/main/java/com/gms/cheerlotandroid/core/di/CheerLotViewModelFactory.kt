package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gms.cheerlotandroid.presentation.main.MiniPlayerViewModel
import com.gms.cheerlotandroid.presentation.main.TeamMembersTestViewModel
import com.gms.cheerlotandroid.presentation.playback.PlaybackViewModel

// 화면별 ViewModel 생성 규칙을 한 곳에 모아두는 Factory입니다.
// 인스턴스 자체는 보관하지 않고 생성 방법만 제공하며, 생명주기는 ViewModelStore가 관리합니다.
class CheerLotViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
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

            // LineupPlaybackViewModel: 라인업 UI를 넘겨받은 뒤 연결 예정 (보류)

            modelClass.isAssignableFrom(TeamMembersTestViewModel::class.java) -> {
                TeamMembersTestViewModel(
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    getAllPlayersUseCase = appContainer.getAllPlayersUseCase,
                    updateSelectedTeamUseCase = appContainer.updateSelectedTeamUseCase,
                    audioPlayer = appContainer.audioPlayer,
                ) as T
            }

            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

/*
새 ViewModel을 추가하는 예시:

override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
        modelClass.isAssignableFrom(LineupViewModel::class.java) -> {
            LineupViewModel(
                getLineupUseCase = appContainer.getLineupUseCase,
            ) as T
        }
        else -> error("Unknown ViewModel class: ${modelClass.name}")
    }
}

사용: val viewModel: LineupViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
*/
