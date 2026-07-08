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

/*
새 dialog를 추가하는 예시:

data class TeamChangeConfirm(
    val teamName: String,
) : CheerLotDialog

사용:

navigator.showDialog(
    CheerLotDialog.TeamChangeConfirm(teamName = "삼성 라이온즈")
)

root DialogHost에서 연결하는 예시:

when (val dialog = navigator.currentDialog) {
    is CheerLotDialog.Confirm -> ConfirmDialog(...)
    is CheerLotDialog.Error -> ErrorDialog(message = dialog.message)
    is CheerLotDialog.TeamChangeConfirm -> TeamChangeConfirmDialog(teamName = dialog.teamName)
    null -> Unit
}

닫을 때:

navigator.dismissDialog()
*/
