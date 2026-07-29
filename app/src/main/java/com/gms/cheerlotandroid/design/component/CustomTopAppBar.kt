package com.gms.cheerlotandroid.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

private val TOP_APP_BAR_HEIGHT = 46.dp
private val DEFAULT_HORIZONTAL_PADDING = 20.dp
private val LARGE_TITLE_HORIZONTAL_PADDING = 16.dp

// enum dispatch 대신 leading/center/trailing 슬롯을 받는 Compose 관용 방식으로 구성했습니다.
// center는 leading/trailing 폭과 무관하게 바 전체 기준 정중앙에 옵니다.
// 상태바 등 safe area 처리는 이 컴포넌트가 아니라 화면(root) 쪽 책임입니다.
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = DEFAULT_HORIZONTAL_PADDING,
    leading: (@Composable () -> Unit)? = null,
    center: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TOP_APP_BAR_HEIGHT)
            .padding(horizontal = horizontalPadding)
    ) {
        if (leading != null) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) { leading() }
        }
        if (center != null) {
            Box(modifier = Modifier.align(Alignment.Center)) { center() }
        }
        if (trailing != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) { trailing() }
        }
    }
}

// leading에 LargeTitle과 trailing에 프로필 버튼을 가지는 TopAppBar
@Composable
fun CustomTopAppBarTitleWithProfile(
    title: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTopAppBar(
        modifier = modifier,
        horizontalPadding = LARGE_TITLE_HORIZONTAL_PADDING,
        leading = { LargeTitleText(title) },
        trailing = { ProfileIconButton(onProfileClick) }
    )
}

// leading에만 LargeTitle을 가지는 TopAppBar
@Composable
fun CustomTopAppBarLargeTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    CustomTopAppBar(
        modifier = modifier,
        horizontalPadding = LARGE_TITLE_HORIZONTAL_PADDING,
        leading = { LargeTitleText(title) }
    )
}

// leading에 cancel 버튼과 center에 경기 정보를 가지는 TopAppBar
@Composable
fun CustomTopAppBarGameInfo(
    date: String,
    teams: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTopAppBar(
        modifier = modifier,
        leading = { CloseIconButton(onClose) },
        center = { GameInfoText(date = date, teams = teams) }
    )
}

// leading에 back 버튼과 center에 inlineTitle을 가지는 TopAppBar
@Composable
fun CustomTopAppBarBackWithTitle(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTopAppBar(
        modifier = modifier,
        leading = { BackIconButton(onBack) },
        center = { InlineTitleText(title) }
    )
}

// leading에 cancel 버튼과 center에 inlineTitle, trailing에 check 버튼을 가지는 TopAppBar
@Composable
fun CustomTopAppBarEditMode(
    title: String,
    onClose: () -> Unit,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTopAppBar(
        modifier = modifier,
        leading = { CloseIconButton(onClose) },
        center = { InlineTitleText(title) },
        trailing = { CheckIconButton(onCheck) }
    )
}

@Composable
private fun LargeTitleText(title: String) {
    Text(
        text = title,
        style = CheerLotTextStyle.B3,
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
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
        contentDescription = "뒤로가기",
        tint = GrayScaleColor.Gray800,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun CloseIconButton(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "닫기",
        tint = GrayScaleColor.Gray800,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun CheckIconButton(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = "완료",
        tint = GrayScaleColor.Gray800,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ProfileIconButton(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.AccountCircle,
        contentDescription = "프로필",
        tint = GrayScaleColor.Gray800,
        modifier = Modifier.clickable(onClick = onClick)
    )
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
