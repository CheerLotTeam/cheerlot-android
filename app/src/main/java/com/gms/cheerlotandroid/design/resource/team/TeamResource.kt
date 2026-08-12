package com.gms.cheerlotandroid.design.resource.team

import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.domain.model.team.TeamId

// Android의 팀별 drawable, mipmap, 컴포넌트 이름을 해석하는 리소스 resolver입니다.
object TeamResource {
    // 재생 중인 팀을 지원하지 않아도 UI를 계속 표시할 수 있도록 커버가 없으면 null을 반환합니다.
    fun coverThumbnailRes(teamId: TeamId): Int? = resourcesForOrNull(teamId)?.coverThumbnailRes

    // 설정 화면의 팀별 런처 아이콘을 반환하며, 지원하지 않는 팀은 기본 아이콘을 사용합니다.
    fun launcherIconRes(teamId: TeamId): Int =
        resourcesForOrNull(teamId)?.launcherIconRes ?: R.mipmap.ic_launcher

    // Manifest에 선언된 팀별 activity-alias의 클래스 suffix를 반환합니다.
    fun appIconAliasSuffix(teamId: TeamId): String? =
        resourcesForOrNull(teamId)?.appIconAliasSuffix

    private fun resourcesForOrNull(teamId: TeamId): TeamResourceSet? {
        return when (TeamAssetCode.fromOrNull(teamId)) {
            TeamAssetCode.HANWHA -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_hh,
                launcherIconRes = R.mipmap.ic_launcher_hanwha,
                appIconAliasSuffix = "HanwhaAlias"
            )
            TeamAssetCode.KIA -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kia,
                launcherIconRes = R.mipmap.ic_launcher_kia,
                appIconAliasSuffix = "KiaAlias"
            )
            TeamAssetCode.KT -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kt,
                launcherIconRes = R.mipmap.ic_launcher_kt,
                appIconAliasSuffix = "KtAlias"
            )
            TeamAssetCode.LG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_lg,
                launcherIconRes = R.mipmap.ic_launcher_lg,
                appIconAliasSuffix = "LgAlias"
            )
            TeamAssetCode.LOTTE -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_lt,
                launcherIconRes = R.mipmap.ic_launcher_lotte,
                appIconAliasSuffix = "LotteAlias"
            )
            TeamAssetCode.NC -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_nc,
                launcherIconRes = R.mipmap.ic_launcher_nc,
                appIconAliasSuffix = "NcAlias"
            )
            TeamAssetCode.DOOSAN -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ds,
                launcherIconRes = R.mipmap.ic_launcher_doosan,
                appIconAliasSuffix = "DoosanAlias"
            )
            TeamAssetCode.SSG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ssg,
                launcherIconRes = R.mipmap.ic_launcher_ssg,
                appIconAliasSuffix = "SsgAlias"
            )
            TeamAssetCode.SAMSUNG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ss,
                launcherIconRes = R.mipmap.ic_launcher_samsung,
                appIconAliasSuffix = "SamsungAlias"
            )
            TeamAssetCode.KIWOOM -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kw,
                launcherIconRes = R.mipmap.ic_launcher_kiwoom,
                appIconAliasSuffix = "KiwoomAlias"
            )
            else -> null
        }
    }
}

private data class TeamResourceSet(
    val coverThumbnailRes: Int,
    val launcherIconRes: Int,
    val appIconAliasSuffix: String
)
