package com.gms.cheerlotandroid.design.team

import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.team.TeamColor
import com.gms.cheerlotandroid.design.color.team.TeamColorSet
import com.gms.cheerlotandroid.design.color.team.TeamPrimaryPalette
import com.gms.cheerlotandroid.design.color.team.TeamSecondaryPalette
import com.gms.cheerlotandroid.domain.model.team.TeamId

data class TeamAsset(
    val teamId: TeamId,
    val assetPrefix: String,
    val colors: TeamColorSet = TeamColor.colorsFor(assetPrefix),
    val primaryPalette: TeamPrimaryPalette = TeamColor.primaryPaletteFor(assetPrefix),
    val secondaryPalette: TeamSecondaryPalette = TeamColor.secondaryPaletteFor(assetPrefix)
) {
    val primaryColor: Color = colors.primary
    val secondaryColor: Color = colors.secondary

    val pageIndicatorUnselectedColor: Color =
        when (assetPrefix) {
            "hh", "kt" -> primaryPalette.color200
            else -> GrayScaleColor.GrayWhite
        }

    // TODO: 이미지 추가시, 설정
    // val coverImageName: String = "${assetPrefix}_cover"

    companion object {
        fun from(teamId: TeamId): TeamAsset {
            return TeamAsset(
                teamId = teamId,
                assetPrefix = assetPrefixFor(teamId.value)
            )
        }

        private fun assetPrefixFor(teamId: String): String {
            return when (teamId.trim().uppercase()) {
                "HANWHA" -> "hh"
                "LG" -> "lg"
                "LOTTE" -> "lt"
                "SAMSUNG" -> "ss"
                "NC" -> "nc"
                "KT" -> "kt"
                "SSG" -> "ssg"
                "DOOSAN" -> "ds"
                "KIWOOM" -> "kw"
                "KIA" -> "kia"
                else -> teamId.trim().lowercase()
            }
        }
    }
}
