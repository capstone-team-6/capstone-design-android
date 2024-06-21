package com.team6.testapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat


class MainActivity : ComponentActivity() {
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_CONNECT
    )

    private val requestCode = 1024

    private fun hasPermission(): Boolean {
        for( permission in permissions) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!hasPermission()) {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }

        WebView.setWebContentsDebuggingEnabled(true)

        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webview)
        webView.settings.run {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            userAgentString = "Chrome/56.0.0.0 Mobile"
            cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webViewClient = WebViewClient()

        webView.webChromeClient =  object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.apply {
                    Log.d("MyWebView", "${message()} -- From line ${lineNumber()} of ${sourceId()}")
                }
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                Log.d("PER", "onPermissionRequest")
                runOnUiThread {
                    Log.d("PER", request.origin.toString())
                    request.grant(request.resources)
                }
            }
        }

        webView.loadUrl("https://capstone-design-team6.web.app/sign-in/child")

        var handler: Handler = Handler()
        webView.addJavascriptInterface(WiFiScanner(this, webView, handler), "Bridge")
    }
}


