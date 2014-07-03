package dev.si.timeismoney.background;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import dev.si.timeismoney.database.DatabaseManager;
import dev.si.timeismoney.utils.MyUtils;

import java.util.List;

public class SimpleService extends Service {

    private final String TAG = "SimpleService::"; // demo
    private final int INTERVAL = 3; // 何秒ごとにチェックするか

    private Boolean isActive = true;
    private Handler mHandler = new Handler();
    private Thread mThread;

    private String[] managedAppNames; // アプリ一覧
    private int dayLog; // 一日の使用時間
    private int hourLog; // 時間帯の使用時間
    private boolean isPrevManaged; // 前のアプリ名が測定アプリかどうか
    private String currentApp; // 現在のアプリ名
    private String prevApp; // 前のアプリ名
    private int onceLimit;
    private int dayLimit;
    private int pastDayLog; // 起動時の一日の使用時間　一回の使用時間を計るため

    private MyUtils utils;
    private int currentWeek;
    private int currentHour;
    
    private DatabaseManager dbManager;

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
                        
                        // 曜日、もしくは時間帯が変わったらデータ更新

                        if (currentHour != utils.getHourOfDay()) {
                        	currentHour = utils.getHourOfDay();
                        	// 時間帯が変わったら時間帯のデータを0にする
                            hourLog = 0;
                            for (String app : managedAppNames) {
                                setUsageHourLog(app, 0);
                            }
                        	
                            if (currentWeek != utils.getDayOfWeek()) {
                            	currentWeek = utils.getHourOfDay();
                            	// 曜日が変わったら曜日のデータを0にする
                            	dayLog = 0;
                            	pastDayLog = 0;
                            	for (String app : managedAppNames) {
                                    setUsageDayLog(app, 0);
                                }
                            }
                        }

                        // 前と同じアプリなら前が対象アプリのみ判定すればいい
                        if (currentApp.equals(prevApp)) {
                            if (isPrevManaged) {
                                showText("DETECTED"); // demo
                                dayLog += INTERVAL;	
                                hourLog += INTERVAL;

                                // 制限を超えていないかチェック
                                checkDayAndAlert(dayLog, dayLimit); // 一日の制限を超えていないか
                                checkOnceAndAlert(dayLog-pastDayLog, onceLimit);

                            } else {
                            }
                        } else {
                            if (isPrevManaged) {
                                // アプリが変わったのでデータを保存する
                                setUsageDayLog(prevApp, dayLog);
                                setUsageHourLog(prevApp, hourLog);
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
                                    dayLog = getUsageDayLog(appName);
                                    pastDayLog = getUsageDayLog(appName);
                                    hourLog = getUsageHourLog(appName);

                                    dayLog += INTERVAL;
                                    hourLog += INTERVAL;
                                    // 制限を超えていないかチェック
                                    checkDayAndAlert(dayLog, dayLimit);

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
     * @return 取得した文字列の配列
     */
    private String[] getManagedAppInfo() {
        return dbManager.appNames();
    }

    /**
     * アプリ名からアプリの一回の使用時間制限を取得
     *
     * @param appName アプリ名
     * @return アプリの一回の使用時間上限
     */
    private int getOnceLimit(String appName) {
        // データベースから読み出す
        return dbManager.select(appName, "onceLimit");
    }

    /**
     * アプリ名からアプリの一日の使用時間制限を取得
     *
     * @param appName アプリ名
     * @return アプリの一日の使用時間上限
     */
    private int getDayLimit(String appName) {
        // データベースから読み出す
        return dbManager.select(appName, "dayLimit");
    }

    /**
     * アプリの使用時間を保存
     *
     * @param appName アプリ名
     * @param time 使用時間
     * @return void
     */
    private void setUsageDayLog(String appName, int time) {
        // データベースへ保存
        int week = utils.getDayOfWeek();
        dbManager.update(appName, week2Col(week), time);
        Log.i("TEST updated", String.valueOf(this.getUsageDayLog(appName)));
    }
    
    private void setUsageHourLog(String appName, int time) {
    	int hour = utils.getHourOfDay();
        dbManager.update(appName, hour2Col(hour), time);
    }

    /**
     * アプリ名から当日の使用時間を取得
     *
     * @param appName アプリ名
     * @return アプリの使用時間
     */
    private int getUsageDayLog(String appName) {
        // データベースから読み出す
        int week = utils.getDayOfWeek();
        // Log.i("WEEK:", week2Col(week));
        return dbManager.select(appName, week2Col(week));
    }
    
    private int getUsageHourLog(String appName) {
    	int hour = utils.getHourOfDay();
    	return dbManager.select(appName, hour2Col(hour));
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
    
    /**
     * 
     * @param week
     * @return
     */
    private String week2Col(int week) {
    	String result = "";
    	switch (week) {
        	case 1:
	            result = "logSun";
	            break;
	        case 2:
	        	result = "logMon";
	        	break;
	        case 3:
	        	result = "logTue";
	            break;
	        case 4:
	        	result = "logWed";
	            break;
	        case 5:
	        	result = "logThu";
	            break;
	        case 6:
	        	result = "logFri";
	            break;
	        case 7:
	        	result = "logSat";
	            break;
	        default:
	            break;
	    }
    	return result;
    }
    
    /**
     * 
     * @param hour
     * @return
     */
    private String hour2Col(int hour) {
    	return "logDay_"+String.valueOf(hour);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初期化
        this.dbManager = new DatabaseManager(getApplicationContext());
        
        this.dayLog = 0;
        this.hourLog = 0;
        this.currentApp = "";
        this.prevApp = "";
        this.onceLimit = 0;
        this.dayLimit = 0;
        this.isPrevManaged = false;
        this.pastDayLog = 0;
        this.utils = new MyUtils();
        this.currentWeek = this.utils.getDayOfWeek();
        this.currentHour = this.utils.getHourOfDay();
        
        dbManager.insert("com.android.email", 20, 60);
        dbManager.insert("com.android.calendar", 20, 60);
        
        this.managedAppNames = this.getManagedAppInfo();
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
        showResult();
        // dbManager.delTable();
        this.isActive = false;
        this.mThread.interrupt();

    }

    private void showResult() {
        Log.i(TAG, "result day:");
        for (String app : managedAppNames) {
            Log.i(app, String.valueOf(getUsageDayLog(app)));
        }
        
        Log.i(TAG, "result hour:");
        for (String app : managedAppNames) {
            Log.i(app, String.valueOf(getUsageDayLog(app)));
        }
    }
}
