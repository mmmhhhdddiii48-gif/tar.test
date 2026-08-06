package com.nukhba.branches.employee.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> pendingFiles;
    private static final int FILE_PICKER_REQUEST = 7402;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TrialGate.Result result = TrialGate.check(this);
        if (!result.active) {
            showLocked(result);
            return;
        }
        showProfessionalApp();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showProfessionalApp() {
        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setOnLongClickListener(v -> true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(100);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return uri == null || !"file".equalsIgnoreCase(uri.getScheme());
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (pendingFiles != null) pendingFiles.onReceiveValue(null);
                pendingFiles = callback;
                try {
                    startActivityForResult(params.createIntent(), FILE_PICKER_REQUEST);
                    return true;
                } catch (Exception ex) {
                    pendingFiles = null;
                    return false;
                }
            }
        });

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICKER_REQUEST && pendingFiles != null) {
            pendingFiles.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            pendingFiles = null;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (webView == null) {
            super.onBackPressed();
            return;
        }
        webView.evaluateJavascript("(window.androidBack?window.androidBack():'exit')", value -> {
            if (value == null || "\"exit\"".equals(value)) MainActivity.super.onBackPressed();
        });
    }

    private void showLocked(TrialGate.Result result) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(dp(28), dp(36), dp(28), dp(36));
        root.setBackgroundColor(Color.rgb(247, 248, 250));

        TextView mark = new TextView(this);
        mark.setText("◆");
        mark.setTextSize(54);
        mark.setTextColor(Color.rgb(7, 155, 156));
        mark.setGravity(android.view.Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText(result.tampered ? "تم إيقاف النسخة التجريبية" : "انتهت مدة التجربة");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(23, 36, 61));
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, dp(16), 0, dp(8));

        TextView message = new TextView(this);
        message.setText(result.message);
        message.setTextSize(14);
        message.setTextColor(Color.rgb(100, 110, 126));
        message.setGravity(android.view.Gravity.CENTER);
        message.setLineSpacing(0, 1.35f);
        message.setPadding(0, 0, 0, dp(22));

        Button close = new Button(this);
        close.setText("إغلاق التطبيق");
        close.setTextColor(Color.WHITE);
        close.setBackgroundColor(Color.rgb(23, 36, 61));
        close.setOnClickListener(v -> finish());

        root.addView(mark, new LinearLayout.LayoutParams(-1, -2));
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        root.addView(message, new LinearLayout.LayoutParams(-1, -2));
        root.addView(close, new LinearLayout.LayoutParams(-1, dp(52)));
        setContentView(root);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
