package com.gms.cheerlotandroid.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
