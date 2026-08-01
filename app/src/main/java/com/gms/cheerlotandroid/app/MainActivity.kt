package com.gms.cheerlotandroid.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

class MainActivity : ComponentActivity() {
    // AppContainer는 Application(프로세스 생명주기)이 소유하므로, Activity는 참조만 가져다 씁니다.
    private val appContainer by lazy {
        (application as CheerLotApplication).appContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        setContent {
            CheerLotTheme {
                CheerLotApp(appContainer = appContainer)
            }
        }
    }
}
