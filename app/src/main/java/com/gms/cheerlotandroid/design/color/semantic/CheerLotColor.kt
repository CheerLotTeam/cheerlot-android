package com.gms.cheerlotandroid.design.color.semantic

import com.gms.cheerlotandroid.design.color.brand.BrandColor
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.system.SystemColor

/**
 * 앱 전반에서 의미 단위로 사용하는 semantic 컬러입니다.
 *
 * 원본 컬러칩을 그대로 다시 노출하지 않고, 실제 UI에서 의미를 갖는 색상만 정의합니다.
 * 원본 팔레트는 `brand`, `grayscale`, `system`, `team` 패키지에서 관리합니다.
 */
object CheerLotColor {
    val SystemChange = SystemColor.Change
    val SystemBg = GrayScaleColor.GrayWhite

    val AppPrimary = BrandColor.Sky600
    val AppSecondary = BrandColor.Seam400
}
