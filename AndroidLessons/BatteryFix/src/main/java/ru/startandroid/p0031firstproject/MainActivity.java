package ru.startandroid.p0031firstproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private Button btnWifi;
    private TextView tv;

    private List<String> apps;

    WifiManager wifiManager;

    private Timer timer;
    private TimerTask task;

    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(intent);

        btnWifi = (Button) findViewById(R.id.btnStart);
        tv = (TextView) findViewById(R.id.textView);
        apps = Arrays.asList("org.telegram.messenger", "com.whatsapp", "com.google.android.youtube", "com.instagram.android", "com.perm.kate.pro");


        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        timer = new Timer();
        task = new MyTimerTask();

        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnWifi.getText().equals("Start")) {
                    btnWifi.setText("Stop");
                    timer.schedule(task, 2000, 2000);

                } else {
                    timer.cancel();
                    btnWifi.setText("Start");
                }
            }
        });
    }

    // Using UsageStatsManager
    public boolean isAnyAppForeground() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
            // Sort the stats by the last time used
            if(stats != null) {
                SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if(!mySortedMap.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*public boolean isForeground() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

        ComponentName cn = taskInfo.get(0).topActivity;
        if(cn != null)
            return true;
        else
            return false;
    }*/



    private String retriveNewApp() {
        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (applist != null && applist.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : applist) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }

            return currentApp;

        }
        else {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            return mm;
        }
    }

    private String getRunningApp() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo task = tasks.get(0);
        ComponentName rootActivity = task.baseActivity;

        return rootActivity.getPackageName();

        /*String currentPackageName = rootActivity.getPackageName();
        for (String app : apps) {
            if (currentPackageName.equals(app)) {
                return true;
            }
        }
        return false;*/
    }

    private static String getLollipopFGAppPackageName(Context ctx) {

        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());

            boolean flag;

            if (queryUsageStats.size() > 0) {
                Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"org.pervacio.pvadiag".equals(stats.getPackageName())) {
                    Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //boolean isForeground = new ForegroundCheckTask().isAppOnForeground(MainActivity.this);
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            String currentApp = getRunningApp();
            if (apps.contains(currentApp)) {
                wifiManager.setWifiEnabled(true);
            } else {
                wifiManager.setWifiEnabled(false);
            }
        }
    }
}
