package dev.si.timeismoney.showApp;

import java.util.ArrayList;
import java.util.List;

import dev.si.timeismoney.R;
import dev.si.timeismoney.database.DatabaseManager;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ShowActivity extends Activity {
	
	private ListView listView = null;
	private CustomData itemData;
	private Drawable icon = null;
	private CustomAdapter customAdapater;
    private DatabaseManager dbManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        this.dbManager = new DatabaseManager(getApplicationContext());

	
		setContentView(R.layout.listview);
		
		// データの作成
        List<CustomData> objects = new ArrayList<CustomData>();

		//リストビューのオブジェクトを取得
        listView = (ListView) findViewById(R.id.MyListView);
/* 
        //リストビュー用のArrayAdapterを作成
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
 */       
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
                	ApplicationInfo ai = list.get(position);
                	dbManager.insert(ai.packageName,1,1);
//                    Uri uri=Uri.fromParts("package",ai.packageName,null);
//                    Intent intent=new Intent(Intent.ACTION_DELETE,uri);
//                    startActivity(intent);
                }
            });
           
        }
        
        
        
        
 
	   
/*		
		//PackageManagerのオブジェクトを取得
        PackageManager pm = this.getPackageManager();
        //インストール済パッケージ情報を取得する
        final List appInfoList = pm.getInstalledApplications(BIND_AUTO_CREATE);
 
        for(ApplicationInfo ai : appInfoList){
            itemData = new CustomData();
            if(ai.loadLabel(pm).toString()!=null){
                //アプリ名取得
                itemData.setTextData(ai.loadLabel(pm).toString());
            }else{
                itemData.setTextData("NoName");
            }
        }
 */
		
		
 /*
  * リストビューをカスタマイズする
  * 
		// リソースに準備した画像ファイルからBitmapを作成しておく
        Bitmap image;
        image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
 
        // データの作成
        List<CustomData> objects = new ArrayList<CustomData>();
        CustomData item1 = new CustomData();
        item1.setImagaData(image);
        item1.setTextData("１つ目〜");
 
        CustomData item2 = new CustomData();
        item2.setImagaData(image);
        item2.setTextData("The second");
 
        CustomData item3 = new CustomData();
        item3.setImagaData(image);
        item3.setTextData("Il terzo");
 
        objects.add(item1);
        objects.add(item2);
        objects.add(item3);
 
        CustomAdapter customAdapater = new CustomAdapter(this, 0, objects);
 
        ListView listView = (ListView)findViewById(R.id.MyListView);
        listView.setAdapter(customAdapater);
*/
        
       
        
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

}
