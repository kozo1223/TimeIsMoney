package dev.si.timeismoney;

import java.util.ArrayList;
import java.util.List;

import dev.si.timeismoney.database.DatabaseManager;
import dev.si.timeismoney.showApp.CustomAdapter;
import dev.si.timeismoney.showApp.CustomData;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView listView = null;
	private CustomData itemData;
	private Drawable icon = null;
	private CustomAdapter customAdapater;
    private DatabaseManager dbManager;
    private List<CustomData> objects;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//データベース初期化
		super.onCreate(savedInstanceState);
		this.dbManager = new DatabaseManager(getApplicationContext());
	    setContentView(R.layout.activity_main);
	
	}
	
	private void setRegisterdAppList(String[] appNames) {
		// データの作成
        objects = new ArrayList<CustomData>();

		//リストビューのオブジェクトを取得
        listView = (ListView) findViewById(R.id.appListView);

        //PackageManagerのオブジェクトを取得
        PackageManager pm = this.getPackageManager();
 
        //インストール済パッケージ情報を取得する
        final List<ApplicationInfo> list = pm.getInstalledApplications(BIND_AUTO_CREATE);
 
        //パッケージ情報をリストビューに追記
        for (ApplicationInfo ai : list) {
        	itemData = new CustomData();
//        	// プリインストールされているアプリを除外する
//        	if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) ==
//        			ApplicationInfo.FLAG_SYSTEM)
//        		continue;

        	for(String appName : appNames){
        		itemData = new CustomData();
        		// インストールされているアプリのみ表示
        		if (ai.packageName.equals(appName)){
        			itemData.setPackageName(ai.packageName);
        			if(ai.loadLabel(pm).toString()!=null){
        				//アプリ名取得
        				itemData.setTextData(ai.loadLabel(pm).toString());
        			}else{
        				itemData.setTextData("NoName");
        			}

        			try {
        				//アプリのアイコン取得
        				icon = pm.getApplicationIcon(ai.packageName);
        			} catch (NameNotFoundException e) {
        				e.printStackTrace();
        			}
        			itemData.setImagaData(icon);

        			objects.add(itemData);

        			customAdapater = new CustomAdapter(this, 0, objects);
        			listView.setAdapter(customAdapater);

        			// リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        				@Override
        				public void onItemClick(AdapterView<?> parent, View view,
        						int position, long id) {
        					CustomData data = objects.get(position);
        					showAppDetail(data.getPackageName());
        				}
        			});

        		}
        	}}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		stopService();
		final String appNames[] = dbManager.appNames();
		setRegisterdAppList(appNames);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		startService();
	}
	
	private void stopService() {
        Intent i = new Intent(this, dev.si.timeismoney.background.SimpleService.class);
        stopService(i);
    }
	
	private void startService() {
        Intent i = new Intent(this, dev.si.timeismoney.background.SimpleService.class);
        startService(i);
    }
	
	public void showAppList(View view) {
        Intent i = new Intent(this, dev.si.timeismoney.showApp.ShowActivity.class);
        startActivity(i);
    }
	
	private void showAppDetail(String appName) {
        Intent i = new Intent(this, dev.si.timeismoney.main.AppDetailActivity.class);
        i.putExtra("name", appName);
        startActivity(i);
    }
	
}