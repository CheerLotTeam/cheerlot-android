package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 화면별 ViewModel 생성 규칙을 한 곳에 모아두는 Factory입니다.
// 인스턴스 자체는 보관하지 않고 생성 방법만 제공하며, 생명주기는 ViewModelStore가 관리합니다.
//
// 화면 진입 시점 값(nav args 등)이 생성자에 필요한 ViewModel은 이곳에 분기 처리하지 않습니다.
// 이 Factory는 modelClass 하나로만 분기하므로 호출마다 다른 인자를 줄 수 없습니다.
// 그런 경우 해당 화면(Composable)에서 viewModelFactory { initializer { ... } }로 직접 생성합니다.
class CheerLotViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}. " +
                "Add its creation logic to CheerLotViewModelFactory."
        )
    }
}
