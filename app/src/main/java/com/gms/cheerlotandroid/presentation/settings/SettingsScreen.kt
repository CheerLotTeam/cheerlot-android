package com.gms.cheerlotandroid.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.team.TeamColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarBackWithTitle
import com.gms.cheerlotandroid.design.component.TeamCard
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo
import com.gms.cheerlotandroid.presentation.settings.component.SettingsMenuCard
import com.gms.cheerlotandroid.presentation.settings.component.SettingsSection

private val supportMenuTitles = listOf("서비스 소개", "쳐랏 팀", "문의하기")

@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    onTapTeamCard: () -> Unit,
    onSelectAppIconMode: (AppIconMode) -> Unit,
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
                AppIconSection(
                    appIconMode = state.appIconMode,
                    currentTeamId = currentTeam?.id,
                    onSelectMode = onSelectAppIconMode
                )
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

// iOS SettingView.appIconContent와 동일하게, 기본/팀 두 옵션 중 하나를 선택하는 토글입니다.
// 팀을 아직 선택하지 않았으면(currentTeamId == null) "팀" 옵션은 기본 아이콘을 미리보기로 씁니다.
@Composable
private fun AppIconSection(
    appIconMode: AppIconMode,
    currentTeamId: TeamId?,
    onSelectMode: (AppIconMode) -> Unit
) {
    val isTeamSelected = appIconMode == AppIconMode.TEAM
    val accentColor = currentTeamId?.let { TeamColor.colorsFor(it).primary } ?: GrayScaleColor.Gray300

    SettingsSection(title = "앱 아이콘") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(GrayScaleColor.Gray000)
                .padding(vertical = 10.dp, horizontal = 60.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppIconOption(
                label = "기본",
                iconRes = R.mipmap.ic_launcher,
                selected = !isTeamSelected,
                accentColor = accentColor,
                onClick = { onSelectMode(AppIconMode.BASE) }
            )
            AppIconOption(
                label = "팀",
                iconRes = currentTeamId?.let(::teamIconRes) ?: R.mipmap.ic_launcher,
                selected = isTeamSelected,
                accentColor = accentColor,
                onClick = { onSelectMode(AppIconMode.TEAM) }
            )
        }
    }
}

@Composable
private fun AppIconOption(
    label: String,
    iconRes: Int,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        // iOS의 saturation(selected ? 1 : 0) / opacity(selected ? 1 : 0.55)와 동일하게,
        // 선택 안 된 쪽은 흑백 처리 + 반투명으로 죽입니다.
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            contentScale = ContentScale.Fit,
            colorFilter = if (selected) {
                null
            } else {
                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
            },
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .alpha(if (selected) 1f else 0.55f)
        )
        Text(
            text = label,
            style = CheerLotTextStyle.SB9,
            color = if (selected) accentColor else GrayScaleColor.Gray300,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun teamIconRes(teamId: TeamId): Int {
    return when (teamId.value.trim().uppercase()) {
        "HANWHA" -> R.mipmap.ic_launcher_hanwha
        "KIA" -> R.mipmap.ic_launcher_kia
        "KT" -> R.mipmap.ic_launcher_kt
        "LG" -> R.mipmap.ic_launcher_lg
        "LOTTE" -> R.mipmap.ic_launcher_lotte
        "NC" -> R.mipmap.ic_launcher_nc
        "DOOSAN" -> R.mipmap.ic_launcher_doosan
        "SSG" -> R.mipmap.ic_launcher_ssg
        "SAMSUNG" -> R.mipmap.ic_launcher_samsung
        "KIWOOM" -> R.mipmap.ic_launcher_kiwoom
        else -> R.mipmap.ic_launcher
    }
}
