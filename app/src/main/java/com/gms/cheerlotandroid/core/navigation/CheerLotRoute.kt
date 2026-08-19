package com.gms.cheerlotandroid.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

// 일반 push 화면(back stack에 쌓이는 화면)에 사용하는 route입니다.
// 메인 탭 전환은 CheerLotMainTab, Sheet/FullScreen 화면은 각각 CheerLotSheet/CheerLotFullScreen을 사용합니다.
sealed interface CheerLotRoute {
    val route: String

    data object Settings : CheerLotRoute {
        override val route: String = "settings"
    }

    data object ServiceInfo : CheerLotRoute {
        override val route: String = "service_info"
    }

    data object MakerInfo : CheerLotRoute {
        override val route: String = "maker_info"
    }

    data object TermsOfService : CheerLotRoute {
        override val route: String = "terms_of_service"
    }

    data object PrivacyPolicy : CheerLotRoute {
        override val route: String = "privacy_policy"
    }

    data object Copyright : CheerLotRoute {
        override val route: String = "copyright"
    }
}

enum class CheerLotMainTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    LINEUP("lineup", "라인업", Icons.Filled.SportsBaseball),
    TEAM_MEMBERS("team_members", "전체선수", Icons.Filled.Group),
    SEARCH("search", "검색", Icons.Outlined.Search),
}
