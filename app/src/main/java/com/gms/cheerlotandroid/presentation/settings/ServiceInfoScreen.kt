package com.gms.cheerlotandroid.presentation.settings

import androidx.compose.foundation.layout.Arrangement
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
internal fun ServiceInfoScreen(
    onTapMainPage: () -> Unit,
    onTapTerms: () -> Unit,
    onTapPrivacy: () -> Unit,
    onTapCopyright: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBarBackWithTitle(title = "서비스 소개", onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SettingsSection(title = "쳐랏 소개") {
                SettingsMenuCard(titles = listOf("대표 페이지"), onTap = { onTapMainPage() })
            }
            SettingsSection(title = "서비스 약관") {
                SettingsMenuCard(
                    titles = listOf("이용약관", "개인정보처리방침", "저작권 법적고지"),
                    onTap = { index ->
                        when (index) {
                            0 -> onTapTerms()
                            1 -> onTapPrivacy()
                            2 -> onTapCopyright()
                        }
                    }
                )
            }
        }
    }
}
