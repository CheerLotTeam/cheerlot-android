package com.gms.cheerlotandroid.presentation.lineupplayback

import kotlin.math.abs

// 바깥 복제본의 페이지를 동일한 아이템이 위치한 중앙 복제본 페이지로 변환합니다.
internal fun calculateCenteredPagerPage(
    settledPage: Int,
    itemCount: Int
): Int? {
    require(itemCount > 0)
    if (itemCount == 1) return null

    return when {
        settledPage < itemCount -> settledPage + itemCount
        settledPage >= itemCount * 2 -> settledPage - itemCount
        else -> null
    }
}

// 현재 페이지를 기준으로 재생할 아이템과 가장 가까운 복제 페이지를 계산합니다.
internal fun calculateNearestPagerPage(
    currentPage: Int,
    itemIndex: Int,
    itemCount: Int
): Int {
    require(itemCount > 0)

    val validItemIndex = itemIndex.coerceIn(0, itemCount - 1)
    if (itemCount == 1) return validItemIndex

    return listOf(
        validItemIndex,
        itemCount + validItemIndex,
        itemCount * 2 + validItemIndex
    ).minBy { candidate -> abs(candidate - currentPage) }
}
