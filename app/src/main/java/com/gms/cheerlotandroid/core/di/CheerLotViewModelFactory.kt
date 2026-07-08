package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 화면별 ViewModel 생성 규칙을 한 곳에 모아두는 Factory입니다.
//
// 새 ViewModel을 추가할 때의 순서:
// 1. ViewModel이 필요한 UseCase/Repository는 AppContainer에 먼저 추가합니다.
// 2. ViewModel은 필요한 의존성을 생성자로 받도록 만듭니다.
// 3. create()의 when 분기에 해당 ViewModel 생성 코드를 추가합니다.
// 4. Composable 화면에서는 LocalAppContainer.current.viewModelFactory를 가져와 viewModel(factory = ...)에 전달합니다.
//
// 이 Factory는 ViewModel 인스턴스를 직접 보관하지 않습니다.
// 실제 ViewModel 생명주기와 재사용은 Activity/NavBackStackEntry의 ViewModelStore가 관리합니다.
class CheerLotViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}. " +
                "Add its creation logic to CheerLotViewModelFactory."
        )
    }
}

/*
새 ViewModel을 추가하는 예시:

override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
        modelClass.isAssignableFrom(TeamSelectViewModel::class.java) -> {
            TeamSelectViewModel(
                getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                updateSelectedTeamUseCase = appContainer.updateSelectedTeamUseCase,
            ) as T
        }

        modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
            SettingsViewModel(
                getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
            ) as T
        }

        else -> error("Unknown ViewModel class: ${modelClass.name}")
    }
}

Composable에서 사용하는 예시:

val factory = LocalAppContainer.current.viewModelFactory
val viewModel: TeamSelectViewModel = viewModel(factory = factory)

원칙:
- ViewModel은 UseCase를 생성자로 받습니다.
- ViewModel에서 RepositoryImpl, Database, Service를 직접 만들지 않습니다.
- ViewModel 인스턴스 보관은 이 Factory가 아니라 Android ViewModelStore가 담당합니다.
*/
