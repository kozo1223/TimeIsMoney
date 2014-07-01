package dev.si.timeismoney;

import dev.si.timeismoney.database.DatabaseManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    public void onStartClick(View view) {
        Intent i = new Intent(this, dev.si.timeismoney.background.SimpleService.class);
        startService(i);
    }

    public void onStopClick(View view) {
        Intent i = new Intent(this, dev.si.timeismoney.background.SimpleService.class);
        stopService(i);
    }

 // データベース作成　テスト用
    private void createDatabase() {
        Log.i("MainActivity.java", "createDatabase is called");
        DatabaseManager dbManager = new DatabaseManager(getApplicationContext());
        dbManager.insert("email", 20, 60);
        dbManager.insert("calendar", 20, 60);
        dbManager.insert("myDevApp", 20, 60);
    }
}
