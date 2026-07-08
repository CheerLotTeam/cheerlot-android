package com.gms.cheerlotandroid.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gms.cheerlotandroid.core.di.AppContainer
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

class MainActivity : ComponentActivity() {
    // Activity 생명주기 안에서 AppContainer를 한 번 생성해 앱 루트로 전달합니다.
    private val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheerLotTheme {
                CheerLotApp(appContainer = appContainer)
            }
        }
    }
}
