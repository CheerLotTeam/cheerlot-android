package com.gms.cheerlotandroid.design.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.gms.cheerlotandroid.R

/**
 * 폰트 패밀리를 Android 리소스 기준으로 정의합니다.
 */
object CheerLotFontFamily {
    val Pretendard = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold)
    )

    val RobotoCondensed = FontFamily(
        Font(R.font.roboto_condensed_black, FontWeight.Black)
    )
}
