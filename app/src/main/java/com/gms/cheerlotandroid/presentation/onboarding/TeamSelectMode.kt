package com.gms.cheerlotandroid.presentation.onboarding

enum class TeamSelectMode {
    ONBOARDING,
    CHANGE;

    val guideText: String
        get() = "응원 팀을 선택해주세요"

    val showsTopBar: Boolean
        get() = this == CHANGE

    val showsBottomButton: Boolean
        get() = this == ONBOARDING

    val navigationTitle: String
        get() = if (this == CHANGE) "팀 변경" else ""
}
