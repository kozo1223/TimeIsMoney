package dev.si.timeismoney.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import dev.si.timeismoney.R;
import dev.si.timeismoney.database.DatabaseManager;
import dev.si.timeismoney.utils.MyUtils;

/**
 * Created by Yagi-mac on 2014/07/05.
 */
public class AppDetailActivity extends Activity implements OnClickListener {
	private DatabaseManager dbManager;
    private MyUtils utils;
    private String name;
	private MyChartView myChartView;
	public enum Type{ WEEK, HOUR }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.app_detail);
	    
	    this.dbManager = new DatabaseManager(this);
        this.utils = new MyUtils();
        Intent intent = getIntent();
        this.name = intent.getStringExtra("name");
        
	    // 初期化
	    myChartView = (MyChartView)findViewById(R.id.sample_chart);
	    Button btnHour = (Button)findViewById(R.id.buttonHour);
	    Button btnWeek = (Button)findViewById(R.id.buttonWeek);
	    
	    btnHour.setOnClickListener(this);
	    btnWeek.setOnClickListener(this);

	    displayChart(name, Type.WEEK);
	}
	
	private void displayChart(String appName, Type type) {
		if (type == Type.WEEK) {
			int[] dataList = getWeekLogs(appName);
			myChartView.createChart(dataList, Type.WEEK);
		} else if (type == Type.HOUR) {
			int[] dataList = getHourLogs(appName);
			myChartView.createChart(dataList, Type.HOUR);
		} else {
			
		}
	}
	
    private int[] getHourLogs(String appName) {
    	int[] data = new int[24];
    	for (int i = 0; i < 24; i++) {
    		data[i] = dbManager.select(appName, utils.hour2Col(i));
    	}
    	return data;
    }
    
    private int[] getWeekLogs(String appName) {
    	int[] data = new int[7];
    	for (int i = 0; i < 7; i++) {
    		data[i] = dbManager.select(appName, utils.week2Col(i+1));
    	}
    	return data;
    }
    
    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.buttonHour:
			displayChart(name, Type.HOUR);
			break;
		case R.id.buttonWeek:
			displayChart(name, Type.WEEK);
			break;
		default:
			break;
		}
    }

}
