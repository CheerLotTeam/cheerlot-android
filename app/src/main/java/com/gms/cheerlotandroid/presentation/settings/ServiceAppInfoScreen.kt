package com.gms.cheerlotandroid.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarBackWithTitle
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

// iOS ServiceAppInfoView와 동일하게, 이용약관/개인정보처리방침/저작권 법적고지 3개 화면이
// 제목+본문만 다르게 이 화면 하나를 재사용합니다.
@Composable
internal fun ServiceAppInfoScreen(
    title: String,
    body: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedBody = remember(body) { body.toBoldAnnotatedString() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBarBackWithTitle(title = title, onBack = onBack)
        }
    ) { padding ->
        Text(
            text = annotatedBody,
            style = CheerLotTextStyle.R2,
            color = GrayScaleColor.Gray400,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        )
    }
}

// iOS의 Text(.init(text))가 자동으로 처리하는 "**굵게**" 마크다운만 지원합니다(그 외 문법은 본문에 없음).
private fun String.toBoldAnnotatedString(): AnnotatedString {
    val boldMarker = "**"
    return buildAnnotatedString {
        var rest = this@toBoldAnnotatedString
        while (true) {
            val start = rest.indexOf(boldMarker)
            if (start == -1) {
                append(rest)
                break
            }
            val end = rest.indexOf(boldMarker, start + boldMarker.length)
            if (end == -1) {
                append(rest)
                break
            }
            append(rest.substring(0, start))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(rest.substring(start + boldMarker.length, end))
            }
            rest = rest.substring(end + boldMarker.length)
        }
    }
}
