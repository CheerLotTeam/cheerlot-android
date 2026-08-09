package com.gms.cheerlotandroid.presentation.settings.component

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.gms.cheerlotandroid.presentation.settings.AppLinks

// iOS "문의하기"의 SafariView 시트와 동일하게, 구글폼을 앱 안에서 바로 보여줍니다.
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun InquiryWebViewSheet(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(AppLinks.INQUIRY_FORM_URL)
            }
        }
    )
}
