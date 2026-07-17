package com.gms.cheerlotandroid.domain.model.team

// 기본으로 보여줄 경기 정보 + 최근 확정 경기 정보를 함께 담습니다.
// 어느 걸 보여줄지는 화면(ViewModel)의 상호작용 상태가 결정하므로, 여기서는 둘 다 항상 채워서 전달합니다.
data class LineupGameInfo(
    // 기본 표시용. 오늘 라인업이 확정됐으면 recentGameInfo와 동일한 값이고, 미확정이면 스케줄 미리보기로 opponent/starterPitcher/isHome/status가 대체됩니다.
    val gameInfo: TeamGameInfo,
    // 라인업이 실제 확정됐던 최근 경기 원본 (가공 없음)
    val recentGameInfo: TeamGameInfo
)
