package dev.si.timeismoney.background;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SimpleService extends Service {

	private final String TAG = "SimpleService::"; // demo
	private final int INTERVAL = 3; // 何秒ごとにチェックするか

    private Boolean isActive = true;
    private Handler mHandler = new Handler();
    private Thread mThread;

    private String[] managedAppNames;
    private int[] managedAppTimes = {0, 0, 0}; // demo
    private int managedAppLog;
    private boolean isPrevManaged;
    private String currentApp;
    private String prevApp;
    private int onceLimit;
    private int dayLimit;
    private int pastLog;

    private Runnable logTask = new Runnable() {
        @Override
        public void run() {
            while (isActive) {
                try {
                    Thread.sleep(INTERVAL * 1000);
                } catch (InterruptedException e) {
                    // エラーハンドリング TODO
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                        List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1);

                        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
                        currentApp = componentInfo.getPackageName();
                        
                        // 前と同じアプリなら前が対象アプリのみ判定すればいい
                        if (currentApp.equals(prevApp)) {
                        	if (isPrevManaged) { 
                        		showText("DETECTED"); // demo
                        		managedAppLog += 3;
                        		
                        		// 制限を超えていないかチェック
                        		checkDayAndAlert(managedAppLog, dayLimit); // 一日の制限を超えていないか
                                checkOnceAndAlert(managedAppLog-pastLog, onceLimit);

                        	} else {
                        	}
                        } else {
                        	if (isPrevManaged) {
                        		// アプリが変わったのでデータを保存する
                        		setUsageTime(prevApp, managedAppLog);
                        	} else {
                        		// アプリが変わったが前のアプリは測定対象ではないのでデータ保存は必要ない
                        	}
                        	
                        	// アプリが変わったので対象アプリか判定する
                        	for (String appName : managedAppNames) {
                                if(currentApp.contains(appName)) {
                                	showText("DETECTED"); // demo
                                	Log.i("detecting ... :", appName); // demo
                                	
                                	// データ入れ替え
                                	onceLimit = getOnceLimit(appName);
                                	dayLimit = getDayLimit(appName);
                                    managedAppLog = getUsageTime(appName);
                                    pastLog = getUsageTime(appName);
                                    
                                    managedAppLog += 3;
                                    // 制限を超えていないかチェック
                                    checkDayAndAlert(managedAppLog, dayLimit);
                                    
                                    isPrevManaged = true;
                                    break;
                                } else {
                                	isPrevManaged = false;
                                }
                            }
                        	prevApp = currentApp;
                        }
                        
                    }
                });
            }
        }
    };

    private void showText(String text) {
        Toast.makeText(this, TAG + text, Toast.LENGTH_SHORT).show();
    }

    /** 
     * データベースから対象アプリ名を取得
     * 
     * @param void
     * @return 取得した文字列の配列
     */
    private String[] getManagedAppInfo() {
    	// TODO
    	String[] appList = new String[3];
        appList[0] = "myDevApp";
        appList[1] = "mail";
        appList[2] = "calendar";

        return appList;
    }
    
    

    /**
     * アプリ名からアプリの一回の使用時間制限を取得
     * 
     * @param アプリ名
     * @return アプリの一回の使用時間上限
     */
    private int getOnceLimit(String appName) {
    	// データベースから読み出す
    	// TODO
    	return 20;
    }
    
    /**
     * アプリ名からアプリの一日の使用時間制限を取得
     * 
     * @param アプリ名
     * @return アプリの一日の使用時間上限
     */
    private int getDayLimit(String appName) {
    	// データベースから読み出す
    	// TODO
    	return 60;
    }
    
    /**
     * アプリの使用時間を保存
     * 
     * @param appName アプリ名
     * @param time 使用時間
     * @return void
     */
    private void setUsageTime(String appName, int time) {
    	// データベースへ保存
    	// TODO
    	
    	// demo
    	for (int i = 0; i < managedAppNames.length; i++) {
    		if (appName.contains(managedAppNames[i])) {
    			managedAppTimes[i] = time;
    		}
    	}
    }
    
    /**
     * アプリ名から当日の使用時間を取得
     * 
     * @param アプリ名
     * @return アプリの使用時間
     */
    private int getUsageTime(String appName) {
    	// データベースから読み出す
    	// TODO
    	
    	// demo
    	int log = 0;
    	for (int i = 0; i < managedAppNames.length; i++) {
    		if (appName.contains(managedAppNames[i])) {
    			log = managedAppTimes[i];
    		}
    	}
    	
    	return log;
    }
    
    private void alert(String message) {
    	// 使用時間が過ぎていることを警告
    	// TODO
    	Log.i("AlertTest", message);
    }
    
    private void checkDayAndAlert(int time, int limit) {
    	if (time > limit) {
    		alert("一日の利用時間を過ぎています");
    	} else {
    	}
    }
    
    // checkDayAndAletとほぼ同じだが、諸々の理由により分けた
    private void checkOnceAndAlert(int time, int limit) {
    	if (time > limit) {
    		alert("一回の利用時間を過ぎています");
    	} else {
    	}
    }
    
    @Override
	public void onCreate() {
        super.onCreate();
        // 初期化
        this.managedAppNames = this.getManagedAppInfo();
        this.managedAppLog = 0;
        this.currentApp = "";
        this.prevApp = "";
        this.onceLimit = 0;
        this.dayLimit = 0;
        this.isPrevManaged = false;
        this.pastLog = 0;

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        this.mThread = new Thread(null, logTask, "NortifyingService");
        this.mThread.start();
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.isActive = false;
        this.mThread.interrupt();
        for (int time : managedAppTimes) {
            Log.i(TAG+"TIME -> ", String.valueOf(time));
        }
	}
}