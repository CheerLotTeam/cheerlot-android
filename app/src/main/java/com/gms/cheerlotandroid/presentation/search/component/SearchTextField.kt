package com.gms.cheerlotandroid.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

// 디자인 시스템에 텍스트 입력 컴포넌트가 아직 없어서 새로 만든 검색창입니다.
// 최대 글자수 제한은 이 컴포넌트가 아니라 SearchViewModel.onQueryChange에서 처리합니다(iOS와 동일하게
// 검색어 자체가 12자로 제한되는 것이지, 이 UI만의 제약이 아니라서).
// 다른 카드류(TeamCard, SettingsMenuCard 등)와 톤을 맞춰 12dp 라운드 + Gray000 배경으로 구성했습니다.
@Composable
internal fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GrayScaleColor.Gray000)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = GrayScaleColor.Gray300,
            modifier = Modifier.size(20.dp)
        )
        Box(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            if (query.isEmpty()) {
                Text(
                    text = "검색어를 입력해주세요",
                    style = CheerLotTextStyle.M4,
                    color = GrayScaleColor.Gray300
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = CheerLotTextStyle.M4.copy(color = GrayScaleColor.Gray800),
                cursorBrush = SolidColor(GrayScaleColor.Gray800),
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "지우기",
                tint = GrayScaleColor.Gray300,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChange("") }
            )
        }
    }
}
