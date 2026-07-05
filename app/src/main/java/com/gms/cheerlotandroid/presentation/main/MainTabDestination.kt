package com.gms.cheerlotandroid.presentation.main

import androidx.compose.ui.graphics.vector.ImageVector

internal enum class MainTabDestination(
    val label: String,
    val icon: ImageVector
) {
    Lineup(
        label = "라인업",
        icon = MainTabIcons.Lineup
    ),
    TeamMembers(
        label = "전체선수",
        icon = MainTabIcons.TeamMembers
    ),
    Search(
        label = "검색",
        icon = MainTabIcons.Search
    )
}
