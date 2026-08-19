package com.gms.cheerlotandroid.design.preview

import androidx.compose.ui.tooling.preview.Preview

// 폭이 작은 기기/큰 기기에서 레이아웃이 안 깨지는지 한 번에 확인하기 위한 MultiPreview입니다.
// sw360dp(작은 폭 기준)와 430dp(큰 폭)로 두 지점만 확인합니다.
@Preview(name = "Small (360dp)", device = "spec:width=360dp,height=640dp")
@Preview(name = "Large (430dp)", device = "spec:width=430dp,height=932dp")
annotation class DevicePreviews
