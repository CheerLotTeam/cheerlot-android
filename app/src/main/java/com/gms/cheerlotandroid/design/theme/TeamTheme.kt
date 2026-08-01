package com.gms.cheerlotandroid.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.gms.cheerlotandroid.design.color.team.TeamColor
import com.gms.cheerlotandroid.design.color.team.TeamColors
import com.gms.cheerlotandroid.domain.model.team.TeamId

// TeamTheme: 결과를 Compose 트리에 공급하는 provider
private val LocalTeamColors = staticCompositionLocalOf<TeamColors> {
    error("TeamTheme이 제공되지 않았습니다.")
}

object TeamTheme {
    val colors: TeamColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTeamColors.current
}

@Composable
fun TeamTheme(
    teamId: TeamId,
    content: @Composable () -> Unit
) {
    val colors = remember(teamId) {
        TeamColor.colorsFor(teamId)
    }

    CompositionLocalProvider(
        LocalTeamColors provides colors,
        content = content
    )
}
