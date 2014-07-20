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
import dev.si.timeismoney.database.PreferencesManager;
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
    private PreferencesManager prefManager;

	/**
	 * 一日の利用制限に達したかどうかの判定
	 * 
	 * @author yamada
	 */
	private int dayFlag; // make yamada 一日の利用制限に達したかどうかの判定
	private int onceFlag; // make yamada 一回の利用制限に達したかどうかの判定
	private int snoozeDayLimit;// make yamada 一日の警告はどのくらいの時間の間隔でするか
	private int snoozeDayLog;// make yamada 一日の警告を出してからの時間
	private int snoozeOnceLimit;// make yamada 一回の警告はどのくらいの時間の間隔でするか
	private int snoozeOnceLog;// make yamada 一回の警告を出してからの時間

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

						currentApp = getCurrentApp();

						// 曜日、もしくは時間帯が変わったらデータ更新
						if (currentHour != utils.getHourOfDay()) {
							for (String app : managedAppNames) {
								// 現在保存している　hourLogの値を保存する
								// 次の時間帯のデータを0にする
								setUsageHourLog(currentHour, app, hourLog);
								setUsageHourLog((currentHour + 1) % 24, app, 0);
							}
							currentHour = utils.getHourOfDay();
							hourLog = 0;

							if (currentWeek != utils.getDayOfWeek()) {
								for (String app : managedAppNames) {
									// 現在保存している dayLogの値を保存する
									// 次の曜日のデータを0にする
									setUsageDayLog(currentWeek, app, dayLog);
									setUsageDayLog(currentWeek % 7 + 1, app, 0);
								}
								currentWeek = utils.getHourOfDay();
								dayLog = 0;
								pastDayLog = 0;
								dayFlag = 0;// make yamada
							}
						}

						// 前と同じアプリなら前が対象アプリのみ判定すればいい
						if (currentApp.equals(prevApp) || currentApp.equals("dev.si.timeismoney")) {
							if (isPrevManaged) {
								// showText("DETECTED"); // demo
								
								dayLog += INTERVAL;
								hourLog += INTERVAL;

								if (onceFlag==1)
									snoozeOnceLog += INTERVAL;// make yamada
								if (dayFlag==1)
									snoozeDayLog += INTERVAL;// make yamada

								// 制限を超えていないかチェック
								// make yamada
									checkSnoozeOnceAlert(snoozeOnceLog,
											snoozeOnceLimit);
									checkOnceAndAlert(dayLog - pastDayLog,
											onceLimit);// 一回の利用制限を超えていないか
									
									checkSnoozeDayAlert(snoozeDayLog,
										snoozeDayLimit);
									checkDayAndAlert(dayLog, dayLimit); // 一日の制限を超えていないか	
									

							} else {
							}
						} else {
							if (isPrevManaged) {
								
								// アプリが変わったのでデータを保存する
								setUsageDayLog(currentWeek, prevApp, dayLog);
								setUsageHourLog(currentHour, prevApp, hourLog);
								
							} else {
								// アプリが変わったが前のアプリは測定対象ではないのでデータ保存は必要ない
							}

							// アプリが変わったので対象アプリか判定する
							for (String appName : managedAppNames) {
								if (currentApp.contains(appName)) {
									showText("DETECTED"); // demo

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

									onceFlag = 0;// make yamada
									

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

	private String getCurrentApp() {
		// 現在起動中のアプリ名を取得
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
				.getRunningTasks(1);

		ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
		return componentInfo.getPackageName();
	}

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
	 * @param appName
	 *            アプリ名
	 * @return アプリの一回の使用時間上限
	 */
	private int getOnceLimit(String appName) {
		// データベースから読み出す
		return dbManager.select(appName, "onceLimit");
	}


    @Override
    public void onCreate() {
        super.onCreate();
        // 初期化
        initVariables();
        initDatabase();
    }
    
    private void initVariables() {
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
//        dbManager.insert("com.android.email", 20, 60);
//        dbManager.insert("com.android.calendar", 20, 60);
        this.currentWeek = this.utils.getDayOfWeek();
        this.currentHour = this.utils.getHourOfDay();
        this.prefManager = new PreferencesManager(getApplicationContext());
        this.managedAppNames = this.getManagedAppInfo();    
        this.onceFlag = 0;// make yamada
		this.dayFlag = 0;// make yamada
		this.snoozeDayLimit = 6;// make yamada
		this.snoozeDayLog = 0;// make yamada
		this.snoozeOnceLimit = 6;// make yamada
		this.snoozeOnceLog = 0;// make yamada         
    }
    
    private void initDatabase() {
    	int day = utils.getDayOfMonth();
    	int week = utils.getDayOfWeek();
    	int hour = utils.getHourOfDay();
    	
    	int prevDay = prefManager.getLastUsedDay();
        int prevHour = prefManager.getLastUsedHour(); 
    	
    	// int prevHour = 10;
        int prevMonth = prefManager.getLastUsedMonth();
    	int prevMonthLength = utils.month2MonthLength(prevMonth);
    	
    	// pref から得た値が-1だったときの処理
    	if (prevDay == -1 || prevHour == -1 || prevMonth == -1) {
    		return;
    	}
    	
    	if (day == prevDay) {
    		// 前回終了した日にちと同じなら時間帯が同じかチェックする
    		if (hour == prevHour) {
    			// 日にちが同じで時間帯が同じならそのままデータベースから読み出せばよい
    		} else {
    			// 時間帯が異なるなら差分のデータを0にする
    			setZeroHourDiff(hour-prevHour, hour);
    		}
    	} else {
    		// 前回終了したときから日にちが離れていた場合、その間の日のデータを0にする
    		// 7日以上離れていれば全曜日のデータを0にする
    		int dayDiff = day - prevDay;
    		
    		if (dayDiff >= 7) {
    			setZeroWeekAndHourAll();
    		} else if (dayDiff > 0){
    			// 月をまたがない場合
    			setZeroWeekAndHourDiff(dayDiff, week);
    		} else {
    			// 月をまたぐ場合、差は負になる 2ヶ月以上またぐ場合は対応しない
    			dayDiff += prevMonthLength;
    			if (dayDiff >= 7) {
    				setZeroWeekAndHourAll();
    			} else {
    				setZeroWeekAndHourDiff(dayDiff, week);
    			}
    		}
    	}
    }
    
    private void setZeroHourDiff(int diff, int hour) {
    	for (String app : managedAppNames) {
			for (int i = diff-1; i >= 0; i--) {
				dbManager.update(app, hour2Col(hour-i), 0);
			}
		}
    }
    
    private void setZeroWeekAndHourAll() {
    	for (String app : managedAppNames) {
			for (int i = 1; i <= 7; i++) {
				dbManager.update(app, week2Col(i), 0);
			}
			for (int i = 0; i < 24; i++) {
				dbManager.update(app, hour2Col(i), 0);
			}
		}
    }
    
    private void setZeroWeekAndHourDiff(int dayDiff, int week) {    	
    	for (String app : managedAppNames) {
			for (int i = dayDiff-1; i >= 0; i--) {
				if (week - i > 0) {
					dbManager.update(app, week2Col(week-i), 0);
				} else {
					dbManager.update(app, week2Col(7+(week-i)), 0);
				}
			}
			for (int i = 0; i < 24; i++) {
				dbManager.update(app, hour2Col(i), 0);
			}
		}
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
	 * アプリの一日の使用時間を保存
	 * 
	 * @param week 保存したい曜日
	 * @param appName アプリ名
	 * @param time 使用時間
	 * 
	 * @return void
	 */
	private void setUsageDayLog(int week, String appName, int time) {
		dbManager.update(appName, week2Col(week), time);
	}

	/**
	 * アプリの時間帯の使用時間を保存
	 * 
	 * @param hour
	 * @param appName
	 * @param time
	 */
	private void setUsageHourLog(int hour, String appName, int time) {
		dbManager.update(appName, hour2Col(hour), time);
	}


    @Override
    public void onDestroy() {
        super.onDestroy();
        showResult();
        setDestroyInfo();
        // dbManager.delTable();
        this.isActive = false;
        this.mThread.interrupt();
    }
    
    private void setDestroyInfo() {
    	prefManager.setLastUsedMonth(utils.getMonth());
    	prefManager.setLastUsedDay(utils.getDayOfMonth());
    	prefManager.setLastUsedWeek(currentWeek);
    	prefManager.setLastUsedHour(currentHour);
    }

	/**
	 * アプリ名から当日の使用時間を取得
	 * 
	 * @param appName アプリ名
	 * @return 一日のアプリの使用時間
	 */
	private int getUsageDayLog(String appName) {
		// データベースから読み出す
		int week = utils.getDayOfWeek();
		return dbManager.select(appName, week2Col(week));
	}
	/**
	 * 
	 * @param appName アプリ名
	 * @return 時間帯のアプリの使用時間
	 */
	private int getUsageHourLog(String appName) {
		int hour = utils.getHourOfDay();
		return dbManager.select(appName, hour2Col(hour));
	}

	private void alert(String message) {
		// 使用時間が過ぎていることを警告
		Intent intent = new Intent(getApplicationContext(),
				CallDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("overTime", message);
		startActivity(intent);
	}

	// change method yamada
	private void checkDayAndAlert(int time, int limit) {
		if (time > limit && dayFlag==0) {
			alert("一日の利用時間を過ぎています");
			dayFlag = 1;// make yamada
		} else {
		}

	}

	// checkDayAndAletとほぼ同じだが、諸々の理由により分けた
	// change method yamada
	private void checkOnceAndAlert(int time, int limit) {
		if (time > limit && onceFlag==0) {
			alert("一回の利用時間を過ぎています");
			onceFlag = 1;// make yamada
		} else {
		}

	}

	// make yamada 一回警告後のスヌーズを行う
	private void checkSnoozeOnceAlert(int time, int limit) {
		if (time > limit && onceFlag==1 && dayFlag==0) {
			alert("一回の利用時間過ぎています");
			snoozeOnceLog = 0;
		} else {
		}
	}

	// make yamada 一日警告後のスヌーズを行う
	private void checkSnoozeDayAlert(int time, int limit) {
		if (time > limit && dayFlag==1) {
			alert("一日の利用時間過ぎています");
			snoozeDayLog = 0;
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
		return "logDay_" + String.valueOf(hour);
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
	
	private void showResult() {
		Log.i(TAG, "result day:");
		for (String app : managedAppNames) {
			Log.i(app, String.valueOf(getUsageDayLog(app)));
		}

		Log.i(TAG, "result hour:");
		for (String app : managedAppNames) {
			Log.i(app, String.valueOf(getUsageHourLog(app)));
		}
	}
}
