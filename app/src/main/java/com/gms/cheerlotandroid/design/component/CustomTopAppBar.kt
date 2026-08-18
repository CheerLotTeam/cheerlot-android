package com.gms.cheerlotandroid.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

// actions이 비어있을 때 navigationIcon과 같은 폭의 투명 Spacer를 넣어 폭을 맞춰줍니다.
private val ICON_BUTTON_SIZE = 48.dp

// leading/center/trailing을 직접 그리는 대신 Material3의 TopAppBar/CenterAlignedTopAppBar에 위임합니다.
// 화면 자신의 Scaffold(topBar = ...)에 넣는 것이 기본 사용법입니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    // 높이는 Material3 기본값(TopAppBarDefaults.TopAppBarExpandedHeight, 64dp)을 그대로 씁니다.
    if (centered) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            windowInsets = windowInsets,
            colors = transparentTopAppBarColors(),
            navigationIcon = navigationIcon,
            title = title,
            actions = actions
        )
    } else {
        TopAppBar(
            modifier = modifier,
            windowInsets = windowInsets,
            colors = transparentTopAppBarColors(),
            navigationIcon = navigationIcon,
            title = title,
            actions = actions
        )
    }
}

// leading에 LargeTitle과 trailing에 프로필 버튼을 가지는 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarTitleWithProfile(
    title: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    CustomTopAppBar(
        modifier = modifier,
        windowInsets = windowInsets,
        title = { LargeTitleText(title) },
        actions = { ProfileIconButton(onProfileClick) }
    )
}

// leading에만 LargeTitle을 가지는 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarLargeTitle(
    title: String,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    CustomTopAppBar(
        modifier = modifier,
        windowInsets = windowInsets,
        title = { LargeTitleText(title) }
    )
}

// leading에 cancel 버튼과 center에 경기 정보를 가지는 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarGameInfo(
    date: String,
    teams: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    CustomTopAppBar(
        modifier = modifier,
        centered = true,
        windowInsets = windowInsets,
        navigationIcon = { CloseIconButton(onClose) },
        title = { GameInfoText(date = date, teams = teams) },
        actions = { NavigationIconSpacer() }
    )
}

// leading에 back 버튼과 center에 inlineTitle을 가지는 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarBackWithTitle(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    CustomTopAppBar(
        modifier = modifier,
        centered = true,
        windowInsets = windowInsets,
        navigationIcon = { BackIconButton(onBack) },
        title = { InlineTitleText(title) },
        actions = { NavigationIconSpacer() }
    )
}

// leading에 cancel 버튼과 center에 inlineTitle, trailing에 check 버튼을 가지는 TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarEditMode(
    title: String,
    onClose: () -> Unit,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier,
    checkColor: Color = GrayScaleColor.Gray800,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    CustomTopAppBar(
        modifier = modifier,
        centered = true,
        windowInsets = windowInsets,
        navigationIcon = { CloseIconButton(onClose) },
        title = { InlineTitleText(title) },
        actions = { CheckIconButton(onClick = onCheck, color = checkColor) }
    )
}

@Composable
private fun transparentTopAppBarColors(): TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)

@Composable
private fun NavigationIconSpacer() {
    Spacer(modifier = Modifier.size(ICON_BUTTON_SIZE))
}

@Composable
private fun LargeTitleText(title: String) {
    Text(
        text = title,
        style = CheerLotTextStyle.B2,
        color = GrayScaleColor.GrayBlack,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun InlineTitleText(title: String) {
    Text(
        text = title,
        style = CheerLotTextStyle.SB7,
        color = GrayScaleColor.GrayBlack,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GameInfoText(date: String, teams: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date,
            style = CheerLotTextStyle.M5,
            color = GrayScaleColor.Gray600,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = teams,
            style = CheerLotTextStyle.SB8,
            color = GrayScaleColor.Gray800,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BackIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = "뒤로가기",
            tint = GrayScaleColor.Gray800
        )
    }
}

@Composable
private fun CloseIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "닫기",
            tint = GrayScaleColor.Gray800
        )
    }
}

@Composable
private fun CheckIconButton(onClick: () -> Unit, color: Color) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "완료",
            tint = color
        )
    }
}

@Composable
private fun ProfileIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "프로필",
            tint = GrayScaleColor.Gray800,
            modifier = Modifier.size(27.dp)
        )
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "TitleWithProfile")
@Composable
private fun CustomTopAppBarTitleWithProfilePreview() {
    CheerLotTheme {
        CustomTopAppBarTitleWithProfile(title = "전체 선수", onProfileClick = {})
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "LargeTitle")
@Composable
private fun CustomTopAppBarLargeTitlePreview() {
    CheerLotTheme {
        CustomTopAppBarLargeTitle(title = "검색")
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "GameInfo")
@Composable
private fun CustomTopAppBarGameInfoPreview() {
    CheerLotTheme {
        CustomTopAppBarGameInfo(date = "2026.07.29", teams = "롯데 vs 삼성", onClose = {})
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "BackWithTitle")
@Composable
private fun CustomTopAppBarBackWithTitlePreview() {
    CheerLotTheme {
        CustomTopAppBarBackWithTitle(title = "설정", onBack = {})
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "EditMode")
@Composable
private fun CustomTopAppBarEditModePreview() {
    CheerLotTheme {
        CustomTopAppBarEditMode(title = "팀 변경", onClose = {}, onCheck = {})
    }
}
