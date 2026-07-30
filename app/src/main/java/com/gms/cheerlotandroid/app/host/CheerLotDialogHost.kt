package com.gms.cheerlotandroid.app.host

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.gms.cheerlotandroid.core.navigation.CheerLotDialog
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState

// presentationState.currentDialog를 관찰해 실제 Dialog로 그려주는 root host입니다.
// 실제 디자인의 Dialog 컴포넌트가 생기면 placeholder를 교체합니다.
@Composable
fun CheerLotDialogHost(presentationState: CheerLotPresentationState) {
    when (val dialog = presentationState.currentDialog) {
        is CheerLotDialog.Confirm -> AlertDialog(
            onDismissRequest = presentationState::dismissDialog,
            title = { Text(dialog.title) },
            text = { Text(dialog.message) },
            confirmButton = {
                TextButton(onClick = presentationState::dismissDialog) { Text("확인") }
            },
        )

        is CheerLotDialog.Error -> AlertDialog(
            onDismissRequest = presentationState::dismissDialog,
            title = { Text("Error") },
            text = { Text(dialog.message) },
            confirmButton = {
                TextButton(onClick = presentationState::dismissDialog) { Text("확인") }
            },
        )

        null -> Unit
    }
}
