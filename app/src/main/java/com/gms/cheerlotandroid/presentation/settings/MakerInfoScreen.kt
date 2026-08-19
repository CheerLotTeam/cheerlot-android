package com.gms.cheerlotandroid.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.component.CustomTopAppBarBackWithTitle
import com.gms.cheerlotandroid.presentation.settings.component.SettingsMenuCard
import com.gms.cheerlotandroid.presentation.settings.component.SettingsSection

@Composable
internal fun MakerInfoScreen(
    onTapInstagram: () -> Unit,
    onTapStoreReview: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBarBackWithTitle(title = "쳐랏 팀", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            SettingsSection(title = "쳐랏 팀") {
                SettingsMenuCard(
                    titles = listOf("쳐랏 인스타그램", "개발자 응원하기"),
                    onTap = { index ->
                        when (index) {
                            0 -> onTapInstagram()
                            1 -> onTapStoreReview()
                        }
                    }
                )
            }
        }
    }
}
