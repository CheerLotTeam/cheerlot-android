package com.gms.cheerlotandroid.design.resource.team

import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.domain.model.team.TeamId

// Android의 팀별 drawable, mipmap, 컴포넌트 이름을 해석하는 리소스 resolver입니다.
object TeamResource {
    // 재생 중인 팀을 지원하지 않아도 UI를 계속 표시할 수 있도록 커버가 없으면 null을 반환합니다.
    fun coverThumbnailRes(teamId: TeamId): Int? = resourcesForOrNull(teamId)?.coverThumbnailRes

    // 설정 화면의 팀별 런처 아이콘 미리보기를 반환하며, 지원하지 않는 팀은 기본 아이콘을 사용합니다.
    // ic_launcher(_<team>)는 API 26+ <adaptive-icon> XML이라 painterResource()로 그릴 수 없어서,
    // Compose Image로 그리는 용도로 배경+foreground를 합성해둔 ic_launcher_preview(_<team>)를 씁니다.
    fun launcherIconRes(teamId: TeamId): Int =
        resourcesForOrNull(teamId)?.launcherIconRes ?: R.mipmap.ic_launcher_preview

    // Manifest에 선언된 팀별 activity-alias의 클래스 suffix를 반환합니다.
    fun appIconAliasSuffix(teamId: TeamId): String? =
        resourcesForOrNull(teamId)?.appIconAliasSuffix

    private fun resourcesForOrNull(teamId: TeamId): TeamResourceSet? {
        return when (TeamAssetCode.fromOrNull(teamId)) {
            TeamAssetCode.HANWHA -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_hh,
                launcherIconRes = R.mipmap.ic_launcher_preview_hanwha,
                appIconAliasSuffix = "HanwhaAlias"
            )
            TeamAssetCode.KIA -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kia,
                launcherIconRes = R.mipmap.ic_launcher_preview_kia,
                appIconAliasSuffix = "KiaAlias"
            )
            TeamAssetCode.KT -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kt,
                launcherIconRes = R.mipmap.ic_launcher_preview_kt,
                appIconAliasSuffix = "KtAlias"
            )
            TeamAssetCode.LG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_lg,
                launcherIconRes = R.mipmap.ic_launcher_preview_lg,
                appIconAliasSuffix = "LgAlias"
            )
            TeamAssetCode.LOTTE -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_lt,
                launcherIconRes = R.mipmap.ic_launcher_preview_lotte,
                appIconAliasSuffix = "LotteAlias"
            )
            TeamAssetCode.NC -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_nc,
                launcherIconRes = R.mipmap.ic_launcher_preview_nc,
                appIconAliasSuffix = "NcAlias"
            )
            TeamAssetCode.DOOSAN -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ds,
                launcherIconRes = R.mipmap.ic_launcher_preview_doosan,
                appIconAliasSuffix = "DoosanAlias"
            )
            TeamAssetCode.SSG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ssg,
                launcherIconRes = R.mipmap.ic_launcher_preview_ssg,
                appIconAliasSuffix = "SsgAlias"
            )
            TeamAssetCode.SAMSUNG -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_ss,
                launcherIconRes = R.mipmap.ic_launcher_preview_samsung,
                appIconAliasSuffix = "SamsungAlias"
            )
            TeamAssetCode.KIWOOM -> TeamResourceSet(
                coverThumbnailRes = R.drawable.team_cover_thumb_kw,
                launcherIconRes = R.mipmap.ic_launcher_preview_kiwoom,
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
