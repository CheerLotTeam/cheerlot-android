package com.gms.cheerlotandroid.core.navigation

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
