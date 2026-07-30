package com.gms.cheerlotandroid.design.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val AUTO_DISMISS_DELAY = 1300.milliseconds
private const val BOTTOM_PADDING_FRACTION = 0.12f

// 화면 전체를 덮는 오버레이지만 clickable이 없어 터치는 그대로 아래로 통과합니다.
@Composable
fun CustomToastMessage(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showCaution: Boolean = true
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(AUTO_DISMISS_DELAY)
            onDismiss()
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.padding(bottom = maxHeight * BOTTOM_PADDING_FRACTION)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 39.5.dp, vertical = 13.5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showCaution) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "오류",
                        tint = GrayScaleColor.GrayWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = message,
                    style = CheerLotTextStyle.M3,
                    color = GrayScaleColor.GrayWhite,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@DevicePreviews
@Preview(showBackground = true)
@Composable
private fun CustomToastMessagePreview() {
    CheerLotTheme {
        CustomToastMessage(
            message = "아직 개인 응원가가 없어요",
            isVisible = true,
            onDismiss = {}
        )
    }
}
