package com.cwh.mvvm_coroutines.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.cwh.mvvm_coroutines_base.utils.ConUtils

import java.io.File

/**
 * Description:
 * Date：2019/11/8 0008-11:11
 * Author: cwh
 */
object WebViewUtils {

    //webview的一些通用设置
    fun webViewSetting(webView: WebView) {
        val webViewSetting: WebSettings = webView.settings
        //webViewSetting.textZoom = 100
        webViewSetting.javaScriptEnabled = true
        webViewSetting.domStorageEnabled = true
        //允许弹窗
        webViewSetting.javaScriptCanOpenWindowsAutomatically = true

        webViewSetting.useWideViewPort = true
        webViewSetting.loadWithOverviewMode = true

        webViewSetting.setGeolocationEnabled(true)

        webViewSetting.allowContentAccess = true
        webViewSetting.allowFileAccess = true
        webViewSetting.setAppCacheEnabled(true)
        webViewSetting.setSupportMultipleWindows(false)
        webViewSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webViewSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        //其他细节操作
        //webViewSetting.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        //webViewSetting.cacheMode=WebSettings.LOAD_NO_CACHE
        webViewSetting.cacheMode=WebSettings.LOAD_DEFAULT
        webViewSetting.allowFileAccess = true //设置可以访问文件
        webViewSetting.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webViewSetting.loadsImagesAutomatically = true //支持自动加载图片
        webViewSetting.defaultTextEncodingName = "utf-8"//设置编码格式
        //webViewSetting.allowUniversalAccessFromFileURLs = true
        //5.0 以后，需要默认不显示https中http的图片的资源，所以要设置为允许混合模式
        webViewSetting.blockNetworkImage = false

        //设置定位的数据库路径
        val dir = ConUtils.mContext().getDir("database", Context.MODE_PRIVATE).path
        webViewSetting.setGeolocationDatabasePath(dir)

        webViewSetting.setGeolocationEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webViewSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            //webViewSetting.mixedContentMode = webViewSetting.mixedContentMode
        }

    }

    fun clearCache(webView: WebView) {
        CookieSyncManager.createInstance(ConUtils.mContext())
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookie()
            cookieManager.flush()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeSessionCookies(null)
            }
            cookieManager.removeAllCookie()
            CookieSyncManager.getInstance().sync()
        }
        //WebStorage.getInstance().deleteAllData()
        webView.clearCache(true)
    }

}