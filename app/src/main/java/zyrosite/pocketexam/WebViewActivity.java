package zyrosite.pocketexam;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progress_value
            ;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl("http://youtube.com");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Toast.makeText(WebViewActivity.this, view.getProgress()+"", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
        progress_value = findViewById(R.id.progress_value);
        progress_value.setProgress(webView.getProgress());
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }
}