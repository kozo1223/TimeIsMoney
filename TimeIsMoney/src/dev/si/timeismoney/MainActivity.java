package dev.si.timeismoney;

import java.util.ArrayList;
import java.util.List;

import dev.si.timeismoney.database.DatabaseManager;
import dev.si.timeismoney.showApp.CustomAdapter;
import dev.si.timeismoney.showApp.CustomData;
import dev.si.timeismoney.utils.MyUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView listView = null;
	private CustomData itemData;
	private Drawable icon = null;
	private CustomAdapter customAdapater;
    private DatabaseManager dbManager;
    private List<CustomData> objects;
    private int resultTime;
    private MyUtils utils;
    private static final int MONEY = 1037;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//データベース初期化
		super.onCreate(savedInstanceState);
		this.dbManager = new DatabaseManager(getApplicationContext());
		this.utils = new MyUtils();
		
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
        			itemData.setPackageName(ai.packageName); // パッケージ名登録
        			resultTime += dbManager.getResultTimeOfWeek(ai.packageName, utils); // 合計時間測定 
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
        					showAppDetail(data.getPackageName(),data.getTextData());
        					
        				}
        			});
        			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        				@Override
        				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        					CustomData data = objects.get(position);
            				onAppClick(data.getPackageName());
        					return true;
        				}
					});

        		}
        	}}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		stopService();
		resultTime = 0;
		final String appNames[] = dbManager.appNames();
		setRegisterdAppList(appNames);
		resultShow();
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
	
	private void showAppDetail(String packageName, String appName) {
        Intent i = new Intent(this, dev.si.timeismoney.main.AppDetailActivity.class);
        i.putExtra("name", packageName);
        i.putExtra("appname",appName);
        startActivity(i);
    }
	
	public void onAppClick(final String appName) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.config_dialog,
				(ViewGroup) findViewById(R.id.layout_root));
		AlertDialog.Builder D = new AlertDialog.Builder(this);
		D.setTitle("アプリの設定");
		D.setMessage("一日の制限、一回の制限を設定して下さい。削除したい場合は削除ボタンを押して下さい。");
		D.setView(layout);
		
		final EditText day = (EditText)layout.findViewById(R.id.setDay);
		day.setInputType(InputType.TYPE_CLASS_NUMBER);
		final EditText once = (EditText)layout.findViewById(R.id.setOnce);
		once.setInputType(InputType.TYPE_CLASS_NUMBER);
		D.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = day.getText().toString();
				// TODO
				int dayLimit = 20;
				if (text != "") {
					dayLimit = Integer.parseInt(text);	
				}
				int onceLimit = 20;
				text = once.getText().toString();
				if (text != "") {
					onceLimit = Integer.parseInt(text);	
				}
				dbManager.update(appName, "dayLimit", dayLimit);
				dbManager.update(appName, "onceLimit", onceLimit);
				Toast.makeText(getApplicationContext(), "変更完了", Toast.LENGTH_SHORT).show();
			}
		});

		D.setNegativeButton("削除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmDelete(appName);
			}
		});
		
		D.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		D.show();
	}
	
	private void confirmDelete(final String appName) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.delete_popup_dialog,
				(ViewGroup) findViewById(R.id.layout_root));
		AlertDialog.Builder D = new AlertDialog.Builder(this);
		D.setTitle("アプリの削除");
		D.setMessage("本当にアプリを管理対象から外しますか？");
		D.setView(layout);
		
		D.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.delete(appName);
				final String appNames[] = dbManager.appNames();
				setRegisterdAppList(appNames);
			}
		});
		
		D.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		D.show();
	}
	
	private void resultShow() {
		TextView textResult = (TextView)findViewById(R.id.textResult);
		String resultText = "";
		double resultHour = resultTime / 3600.0;
		if (resultHour < 0) {
			resultText = "今週は合計で" + String.valueOf((int)-resultHour*MONEY) + "円無駄にしています！";	
		} else {
			resultText = "今週は合計で" + String.valueOf((int)(resultHour*MONEY)) + "円節約しています！";
		}
		textResult.setText(resultText);
	}
	

}
