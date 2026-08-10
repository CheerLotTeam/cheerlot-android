package com.gms.cheerlotandroid.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

class MainActivity : ComponentActivity() {
    // AppContainer는 Application(프로세스 생명주기)이 소유하므로, Activity는 참조만 가져다 씁니다.
    private val appContainer by lazy {
        (application as CheerLotApplication).appContainer
    }

    // 알림 권한이 없어도 재생 자체(포그라운드 서비스)는 동작하지만, 재생 중 알림/잠금화면 컨트롤이
    // 안 보이게 되므로 API 33+에서 요청합니다. 거부해도 앱 동작에는 지장 없어 결과는 무시합니다.
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        requestNotificationPermissionIfNeeded()
        setContent {
            CheerLotTheme {
                CheerLotApp(appContainer = appContainer)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // 런처 아이콘 전환(activity-alias 활성 상태 변경)은 그 즉시 시스템이 앱을 포그라운드에서
    // 내려버리는 부작용이 있어서, 어차피 화면을 벗어나는 이 시점까지 미뤄서 반영합니다.
    override fun onPause() {
        super.onPause()
        appContainer.appIconSwitcher.applyPending()
    }
}
