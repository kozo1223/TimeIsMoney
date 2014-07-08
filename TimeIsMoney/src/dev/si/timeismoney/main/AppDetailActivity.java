package dev.si.timeismoney.main;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import dev.si.timeismoney.R;
import dev.si.timeismoney.database.DatabaseManager;
import dev.si.timeismoney.utils.MyUtils;

/**
 * Created by Yagi-mac on 2014/07/05.
 */
public class AppDetailActivity extends Activity {

    private WebView myWebView;
    private DatabaseManager dbManager;
    private MyUtils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        initWebView();
        this.dbManager = new DatabaseManager(this);
        this.utils = new MyUtils();
    }

    private void initWebView() {
        this.myWebView = (WebView)this.findViewById(R.id.webView);
        this.myWebView.getSettings().setJavaScriptEnabled(true);
        this.myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return checkCallbackUrl(url);
            }
        } );

        this.myWebView.loadUrl( "file:///android_asset/graph.html" );
    }

    private String getLogTime(String appName, int week) {
        int time = dbManager.select(appName, utils.week2Col(week));
        return String.valueOf(time);
    }
    
    private String getLogHour(String appName, int hour) {
    	int time = dbManager.select(appName, utils.hour2Col(hour));
    	return String.valueOf(time);
    }
    
    private void executeJavaScriptFunction(String appName) {
        String sun = getLogTime(appName, 1);
        String mon = getLogTime(appName, 2);
        String tue = getLogTime(appName, 3);
        String wed = getLogTime(appName, 4);
        String thu = getLogTime(appName, 5);
        String fri = getLogTime(appName, 6);
        String sat = getLogTime(appName, 7);
        final String script = "javascript:var myLine = showChart(%s,%s,%s,%s,%s,%s,%s);";
        this.myWebView.loadUrl(String.format(script, sun, mon, tue, wed, thu, fri, sat));
    }

    private boolean checkCallbackUrl(String url) {
        final String callbacScheme = "app-tim-callback://";
        if(!url.startsWith(callbacScheme)) {
            return false;
        }
        executeJavaScriptFunction("com.android.email");
        return true;
    }

}
