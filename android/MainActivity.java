package com.turtlesoup;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 海龟汤推理游戏 Android 客户端
 * 使用 WebView 加载后端 H5 页面
 *
 * 使用方法：
 * 1. 在 Android Studio 中创建新项目
 * 2. 将此文件替换 MainActivity.java
 * 3. 在 AndroidManifest.xml 中添加 INTERNET 权限
 * 4. 修改 BASE_URL 为后端服务器地址
 */
public class MainActivity extends AppCompatActivity {

    // 修改为你的后端服务器地址（本地调试用 10.0.2.2 映射到宿主机的 localhost）
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(BASE_URL + "/login.html");
    }
}
