package com.gms.cheerlotandroid.presentation.appflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val SPLASH_ASSET_NAME = "cheerlot_splash.lottie"

// 애니메이션이 끝난 뒤 0.5초 더 대기했다가 완료 콜백을 호출합니다.
private val POST_ANIMATION_DELAY = 500.milliseconds

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(SPLASH_ASSET_NAME))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )
    val isAnimationComplete = composition != null && progress >= 1f

    LaunchedEffect(isAnimationComplete) {
        if (isAnimationComplete) {
            delay(POST_ANIMATION_DELAY)
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    CheerLotTheme {
        SplashScreen(onFinished = {})
    }
}
