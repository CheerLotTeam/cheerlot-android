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
    Lineup(
        label = "라인업",
        icon = Icons.Filled.SportsBaseball
    ),
    TeamMembers(
        label = "전체선수",
        icon = Icons.Filled.Group
    ),
    Search(
        label = "검색",
        icon = Icons.Outlined.Search
    )
}
