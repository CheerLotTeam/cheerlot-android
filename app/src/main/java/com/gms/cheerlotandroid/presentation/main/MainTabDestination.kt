package com.gms.cheerlotandroid.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

internal enum class MainTabDestination(
    val label: String,
    val icon: ImageVector
) {
    LINEUP(
        label = "라인업",
        icon = Icons.Filled.SportsBaseball
    ),
    TEAM_MEMBERS(
        label = "전체선수",
        icon = Icons.Filled.Group
    ),
    SEARCH(
        label = "검색",
        icon = Icons.Outlined.Search
    )
}
