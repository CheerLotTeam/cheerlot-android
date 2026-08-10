package com.gms.cheerlotandroid.core.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.model.team.TeamId

// Android는 iOS의 alternateIconName 같은 런처 아이콘 전환 API가 없어, activity-alias를
// 미리 등록해두고 그중 하나만 켜고 나머지는 꺼서 아이콘을 바꿉니다.
// MainActivity 자신은 절대 비활성화하지 않습니다 — 모든 alias의 targetActivity가
// MainActivity라서, 실제 컴포넌트가 꺼지면 alias까지 전부 실행 불가 상태(
// "Activity class ... does not exist")가 되기 때문입니다.
class AppIconSwitcher(private val context: Context) {

    // iOS SettingViewModel.applyCurrentAppIcon과 동일하게, mode가 TEAM일 때만 팀 아이콘을
    // 적용하고 BASE거나 팀 미선택 상태면 항상 기본 아이콘으로 돌아갑니다.
    fun switchTo(teamId: TeamId?, mode: AppIconMode) {
        val target = if (mode == AppIconMode.TEAM) {
            teamId?.let { aliasClassNameFor(it) } ?: DEFAULT_ALIAS_CLASS_NAME
        } else {
            DEFAULT_ALIAS_CLASS_NAME
        }
        (aliasClassNames + DEFAULT_ALIAS_CLASS_NAME).forEach { className ->
            setEnabled(className, enabled = className == target)
        }
    }

    private fun setEnabled(className: String, enabled: Boolean) {
        val componentName = ComponentName(context.packageName, className)
        val newState = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        // 상태가 이미 같으면 건너뛰어 불필요한 PackageManager 갱신을 피합니다.
        if (context.packageManager.getComponentEnabledSetting(componentName) == newState) return
        context.packageManager.setComponentEnabledSetting(
            componentName,
            newState,
            PackageManager.DONT_KILL_APP
        )
    }

    companion object {
        private const val ALIAS_PACKAGE_PREFIX = "com.gms.cheerlotandroid.app.icon."
        private const val DEFAULT_ALIAS_CLASS_NAME = "${ALIAS_PACKAGE_PREFIX}DefaultAlias"

        private val aliasClassNames = listOf(
            "${ALIAS_PACKAGE_PREFIX}HanwhaAlias",
            "${ALIAS_PACKAGE_PREFIX}KiaAlias",
            "${ALIAS_PACKAGE_PREFIX}KtAlias",
            "${ALIAS_PACKAGE_PREFIX}LgAlias",
            "${ALIAS_PACKAGE_PREFIX}LotteAlias",
            "${ALIAS_PACKAGE_PREFIX}NcAlias",
            "${ALIAS_PACKAGE_PREFIX}DoosanAlias",
            "${ALIAS_PACKAGE_PREFIX}SsgAlias",
            "${ALIAS_PACKAGE_PREFIX}SamsungAlias",
            "${ALIAS_PACKAGE_PREFIX}KiwoomAlias"
        )

        private fun aliasClassNameFor(teamId: TeamId): String? {
            val suffix = when (teamId.value.trim().uppercase()) {
                "HANWHA" -> "HanwhaAlias"
                "KIA" -> "KiaAlias"
                "KT" -> "KtAlias"
                "LG" -> "LgAlias"
                "LOTTE" -> "LotteAlias"
                "NC" -> "NcAlias"
                "DOOSAN" -> "DoosanAlias"
                "SSG" -> "SsgAlias"
                "SAMSUNG" -> "SamsungAlias"
                "KIWOOM" -> "KiwoomAlias"
                else -> return null
            }
            return "$ALIAS_PACKAGE_PREFIX$suffix"
        }
    }
}
