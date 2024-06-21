package com.team6.testapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebMessage
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject


class WiFiScanner(
    private val context: Context,
    private val webView: WebView,
    private val handler: Handler
) {
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                postMsg()
            } else {
                scanFailure()
            }
        }
    }
    private val intentFilter = IntentFilter()

    init {
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)


    }
    @JavascriptInterface
    fun startScan() {
        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanFailure() {
        val results = wifiManager.scanResults
    }

    @SuppressLint("MissingPermission")
    fun postMsg() {
        val results = wifiManager.scanResults
        val jsonArray = JSONArray()

        for (result in results) {
            val jsonObject = JSONObject()
            jsonObject.put("BSSID", result.BSSID)
            jsonObject.put("SSID", result.SSID)
            jsonObject.put("level",  result.level)
            jsonObject.put("timestamp", result.timestamp)

            jsonArray.put(jsonObject)
        }
        webView.postWebMessage(WebMessage(jsonArray.toString()), Uri.parse("*"))
    }
}

