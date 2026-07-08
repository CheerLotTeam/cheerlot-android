package com.gms.cheerlotandroid.core.navigation

sealed interface CheerLotDialog {
    data class Confirm(
        val title: String,
        val message: String
    ) : CheerLotDialog

    data class Error(
        val message: String
    ) : CheerLotDialog
}
