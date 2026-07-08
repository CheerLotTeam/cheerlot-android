package com.gms.cheerlotandroid.core.navigation

// 일반 push 화면 이동에 사용하는 route입니다.
//
// 새 화면을 추가할 때의 기준:
// - 설정, 약관, 상세 화면처럼 back stack에 쌓이는 화면은 CheerLotRoute에 추가합니다.
// - 라인업/팀 멤버/검색처럼 메인 화면 내부 탭은 CheerLotMainTab에 추가합니다.
// - Bottom Sheet로 열리는 화면은 CheerLotSheet에 추가합니다.
// - 전체 화면 modal로 열리는 재생 화면은 CheerLotFullScreen에 추가합니다.
//
// argument가 없는 화면은 data object로 정의하고,
// argument가 필요한 화면은 data class와 route pattern/createRoute 규칙을 함께 정의합니다.
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
    val route: String
) {
    LINEUP("lineup"),
    TEAM_MEMBERS("team_members"),
    SEARCH("search"),
}

/*
새 route를 추가하는 예시:

1. argument가 없는 push 화면

data object Notice : CheerLotRoute {
    override val route: String = "notice"
}

추가 후 CheerLotNavHost에 연결:

composable(CheerLotRoute.Notice.route) {
    NoticeScreen(...)
}

사용:

navigator.navigate(CheerLotRoute.Notice)

2. argument가 있는 push 화면

data class PlayerDetail(
    val playerId: String,
) : CheerLotRoute {
    override val route: String = "$ROUTE/$playerId"

    companion object {
        const val ROUTE = "player_detail"
        const val PLAYER_ID = "playerId"
        const val PATTERN = "$ROUTE/{$PLAYER_ID}"
    }
}

추가 후 CheerLotNavHost에 연결:

composable(CheerLotRoute.PlayerDetail.PATTERN) { backStackEntry ->
    val playerId = backStackEntry.arguments?.getString(CheerLotRoute.PlayerDetail.PLAYER_ID)
    PlayerDetailScreen(...)
}

사용:

navigator.navigate(CheerLotRoute.PlayerDetail(playerId = "1234"))
*/
