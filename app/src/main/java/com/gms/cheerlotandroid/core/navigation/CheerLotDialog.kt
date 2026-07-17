package com.gms.cheerlotandroid.core.navigation

// Dialog로 표시할 modal 흐름입니다.
sealed interface CheerLotDialog {
    data class Confirm(
        val title: String,
        val message: String
    ) : CheerLotDialog

    data class Error(
        val message: String
    ) : CheerLotDialog
}
