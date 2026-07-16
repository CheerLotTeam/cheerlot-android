package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 화면별 ViewModel 생성 규칙을 한 곳에 모아두는 Factory입니다.
// 인스턴스 자체는 보관하지 않고 생성 방법만 제공하며, 생명주기는 ViewModelStore가 관리합니다.
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
