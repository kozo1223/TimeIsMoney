package dev.si.timeismoney.showApp;

import java.util.ArrayList;
import java.util.List;

import dev.si.timeismoney.R;
import dev.si.timeismoney.database.DatabaseManager;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class ShowActivity extends Activity {
	
	private ListView listView = null;
	private CustomData itemData;
	private Drawable icon = null;
	private CustomAdapter customAdapater;
    private DatabaseManager dbManager;
    private List<CustomData> objects;

    private View layout; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	this.dbManager = new DatabaseManager(getApplicationContext());


    	setContentView(R.layout.listview);

    	// データの作成
    	objects = new ArrayList<CustomData>();

    	//リストビューのオブジェクトを取得
    	listView = (ListView) findViewById(R.id.MyListView);

    	//PackageManagerのオブジェクトを取得
    	PackageManager pm = this.getPackageManager();

    	//インストール済パッケージ情報を取得する
    	final List<ApplicationInfo> list = pm.getInstalledApplications(BIND_AUTO_CREATE);

    	//パッケージ情報をリストビューに追記
    	for (ApplicationInfo ai : list) {
    		itemData = new CustomData();

    		// プリインストールされているアプリを除外する
    		if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) ==
    				ApplicationInfo.FLAG_SYSTEM)
    			continue;

    		// インストールされているアプリのみ表示
    		if (ai.loadLabel(pm).toString().equals("Time is Money")) continue;

    		if (ai.loadLabel(pm).toString().equals("Simple")) continue;

    		if(ai.loadLabel(pm).toString()!=null){
    			//アプリ名取得
    			itemData.setTextData(ai.loadLabel(pm).toString());
    			itemData.setPackageName(ai.packageName);
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
    				//アプリの名前取得
    				CustomData data = objects.get(position);
    				// dbManager.insert(data.getTextData(),1,1);
    				onAppClick(data.getPackageName());
    			}
    		});

    	}
    }
	
    public void onAppClick(final String appName) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.popupdialog,
				(ViewGroup) findViewById(R.id.layout_root));
		AlertDialog.Builder D = new AlertDialog.Builder(this);
		D.setTitle("アプリの登録");
		D.setView(layout);
		final EditText day = (EditText)layout.findViewById(R.id.setDay);
		day.setInputType(InputType.TYPE_CLASS_NUMBER);
		final EditText once = (EditText)layout.findViewById(R.id.setOnce);
		once.setInputType(InputType.TYPE_CLASS_NUMBER);
		D.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// EditText day = (EditText)findViewById(R.id.setDay);
				// EditText once = (EditText)findViewById(R.id.setOnce);
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
				dbManager.insert(appName, dayLimit, onceLimit);
			}
		});
		D.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public void back(View view) {
		finish();
	}
}
