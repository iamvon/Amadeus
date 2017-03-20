package com.example.yink.amadeus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    final String TAG = "Amadeus.Alarm";
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    TimePicker alarmTimePicker;
    ToggleButton alarmToggle;
    public static final int alarmCode = 104856;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, alarmCode, alarmIntent, PendingIntent.FLAG_NO_CREATE);

        if (settings.getBoolean("alarm_toggle", false)) {
            alarmToggle.setChecked(true);
        } else {
            alarmToggle.setChecked(false);
        }
    }

    public void onToggleClicked(View view) {
        editor = settings.edit();
        if (alarmToggle.isChecked()) {
            editor.putBoolean("alarm_toggle", true);
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Current API functions have been executed");
                setTime(calendar);
            } else {
                Log.d(TAG, "Legacy API functions have been executed");
                setTimeLegacy(calendar);
            }
            Log.d(TAG, "Alarm On");
        } else {
            AlarmReceiver.stopRingtone(this);
            editor.putBoolean("alarm_toggle", false);
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Alarm Off");
        }
        editor.apply();
    }

    @SuppressWarnings("deprecation")
    public void setTimeLegacy(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentHour());
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Alarm has been set for " + alarmTimePicker.getCurrentHour() + " hour(s) " + alarmTimePicker.getCurrentHour() + " minute(s)", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Alarm has been set for " + alarmTimePicker.getHour() + " hour(s) " + alarmTimePicker.getMinute() + " minute(s)", Toast.LENGTH_SHORT).show();
    }
}