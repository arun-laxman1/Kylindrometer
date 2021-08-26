package com.example.kylindrometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button switchToManualActivity;
    Button switchToGraphActivity;
    Button switchToAutoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startAlarm(true,true);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Intent notificationIntent = new Intent(this, com.example.kylindrometer.AlarmReceiver.class);
//        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 13);
//        calendar.set(Calendar.MINUTE, 50);
//        calendar.set(Calendar.SECOND, 0);
//
////        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
////            calendar.add(Calendar.DAY_OF_MONTH, 1);
////        }
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, broadcast);
////        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 43200000, broadcast);
////        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, broadcast);

        switchToAutoActivity = findViewById(R.id.button);
        switchToAutoActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection() == true) {
                    switchActivities_auto();
                }
                else {
                    Toast.makeText(MainActivity.this,"Make sure you have internet connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        switchToManualActivity = findViewById(R.id.button2);
        switchToManualActivity.setOnClickListener(new View.OnClickListener() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
            String currentDate = sdf.format(new Date());
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection() == true) {
                    DatabaseReference reff_two = FirebaseDatabase.getInstance().getReference().child("Entries");
                    reff_two.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(currentDate)) {
                                switchActivities_manual_over();
                            }
                            else {
                                switchActivities_manual();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this,"Make sure you have internet connection!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }
        });
        switchToGraphActivity = findViewById(R.id.button3);
        switchToGraphActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection() == true) {
                    switchActivities_graph();
                }
                else {
                    Toast.makeText(MainActivity.this,"Make sure you have internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void switchActivities_auto() {
        Intent switchActivityIntent = new Intent(this, com.example.kylindrometer.k_v1_auto.class);
        startActivity(switchActivityIntent);
    }
    private void switchActivities_manual() {
        Intent switchActivityIntent = new Intent(this, com.example.kylindrometer.k_v1_manual.class);
        startActivity(switchActivityIntent);
        finish();
    }
    private void switchActivities_manual_over() {
        Intent switchActivityIntent = new Intent(this, com.example.kylindrometer.k_v1_manual_over.class);
        startActivity(switchActivityIntent);
    }
    private void switchActivities_graph() {
        Intent switchActivityIntent = new Intent(this, com.example.kylindrometer.k_v1_graph.class);
        startActivity(switchActivityIntent);
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void startAlarm(boolean isNotification, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        // SET TIME HERE
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,20);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);


        myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);

        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000,pendingIntent);
        else {
//            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 43200000, pendingIntent);
        }
    }
}