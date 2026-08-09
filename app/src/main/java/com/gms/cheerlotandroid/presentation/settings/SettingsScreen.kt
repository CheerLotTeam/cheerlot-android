package com.gms.cheerlotandroid.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarBackWithTitle
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamInfo
import com.gms.cheerlotandroid.presentation.settings.component.SettingsMenuCard
import com.gms.cheerlotandroid.presentation.settings.component.SettingsSection
import com.gms.cheerlotandroid.presentation.teammembers.component.TeamCard

private val supportMenuTitles = listOf("서비스 소개", "쳐랏 팀", "문의하기")

@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    onTapTeamCard: () -> Unit,
    onTapServiceInfo: () -> Unit,
    onTapMakerInfo: () -> Unit,
    onTapInquiry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GrayScaleColor.GrayWhite,
        topBar = {
            CustomTopAppBarBackWithTitle(title = "설정", onBack = onBack)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val currentTeam = state.currentTeam
                if (currentTeam != null) {
                    MyTeamSection(team = currentTeam, onTapTeamCard = onTapTeamCard)
                }
                SettingsSection(title = "지원") {
                    SettingsMenuCard(
                        titles = supportMenuTitles,
                        onTap = { index ->
                            when (index) {
                                0 -> onTapServiceInfo()
                                1 -> onTapMakerInfo()
                                2 -> onTapInquiry()
                            }
                        }
                    )
                }
            }

            Text(
                text = "쳐랏 | App Version ${state.appVersion}",
                style = CheerLotTextStyle.M6,
                color = GrayScaleColor.Gray200,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Composable
private fun MyTeamSection(team: TeamInfo, onTapTeamCard: () -> Unit) {
    SettingsSection(title = "나의 팀") {
        TeamTheme(teamId = team.id) {
            TeamCard(team = team, onClick = onTapTeamCard)
        }
    }
}
