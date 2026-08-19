package com.gms.cheerlotandroid.core.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.gms.cheerlotandroid.design.resource.team.TeamResource
import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.model.team.TeamId

// Android는 iOS의 alternateIconName 같은 런처 아이콘 전환 API가 없어, activity-alias를
// 미리 등록해두고 그중 하나만 켜고 나머지는 꺼서 아이콘을 바꿉니다.
// MainActivity 자신은 절대 비활성화하지 않습니다 — 모든 alias의 targetActivity가
// MainActivity라서, 실제 컴포넌트가 꺼지면 alias까지 전부 실행 불가 상태(
// "Activity class ... does not exist")가 되기 때문입니다.
class AppIconSwitcher(private val context: Context) {

    // PackageManager로 컴포넌트 활성 상태를 바꾸는 순간 시스템이 앱을 포그라운드에서 내려버려서,
    // 요청 시점엔 원하는 상태만 기억해두고 실제 반영은 화면을 벗어나는 시점(applyPending)까지
    // 미룹니다. 그때는 어차피 화면을 나가는 중이라 사용자가 "튕겼다"고 느끼지 않습니다.
    // (참고: https://medium.com/madoc-developer/android-배포-없이-app-icon-변경하기-2a8ca63ecae6)
    private var pending: Pair<TeamId?, AppIconMode>? = null

    // iOS SettingViewModel.applyCurrentAppIcon과 동일하게, mode가 TEAM일 때만 팀 아이콘을
    // 적용하고 BASE거나 팀 미선택 상태면 항상 기본 아이콘으로 돌아갑니다.
    fun requestSwitch(teamId: TeamId?, mode: AppIconMode) {
        pending = teamId to mode
    }

    // MainActivity.onPause()에서 호출합니다. 대기 중인 요청이 없으면 아무 일도 하지 않습니다.
    fun applyPending() {
        val (teamId, mode) = pending ?: return
        pending = null
        switchTo(teamId, mode)
    }

    private fun switchTo(teamId: TeamId?, mode: AppIconMode) {
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
            val suffix = TeamResource.appIconAliasSuffix(teamId) ?: return null
            return "$ALIAS_PACKAGE_PREFIX$suffix"
        }
    }
}
