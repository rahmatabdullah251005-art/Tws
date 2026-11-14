package id.mutqin.hafalan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(6, 78, 59));

        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(247, 248, 244));

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());

        root.addView(web, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(root);

        if (Build.VERSION.SDK_INT >= 35) {
            Api35Helper.applyInsets(root);
        } else {
            applyLegacyBarColors();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Api30Helper.setLightSystemBarIcons(getWindow());
        }

        web.loadUrl("file:///android_asset/index.html");
    }

    @SuppressWarnings("deprecation")
    private void applyLegacyBarColors() {
        getWindow().setStatusBarColor(Color.rgb(6, 78, 59));
        getWindow().setNavigationBarColor(Color.rgb(5, 46, 36));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private static class Api35Helper {
        static void applyInsets(View root) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return WindowInsets.CONSUMED;
            });
        }
    }

    private static class Api30Helper {
        static void setLightSystemBarIcons(Window window) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        }
    }
}