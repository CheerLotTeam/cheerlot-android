package com.gms.cheerlotandroid.app

import android.app.Application
import com.gms.cheerlotandroid.core.di.AppContainer

// AppContainer를 Activity가 아니라 앱 프로세스 생명주기에 묶습니다.
// 앱 전역에서 하나만 유지되어야 하는 상태를 여기서 보장합니다.
class CheerLotApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }
}
