package com.tencrm.smsreceiver2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String server_url = "http://khajababa.online/tencrmbank_new/api/tpgpayment";

    private Timer myTimer;
    final String username = "tencrmbank";
    final String password = "Dhaka@1230";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PRECISE_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE
            }, 100);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, 12);
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            final String from = extras.getString("from");
            final String message = extras.getString("message");

          //  smsSync(from, message, username, password);
        }
        // timer

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
              //  testTimer();
            }

        }, 0, 10000);

        // Arekta Scheduel task //
        ScheduledExecutorService scheduleTaskExecutor;
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        //Schedule a task to run every 5 seconds (or however long you want)
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Uri uriSms = Uri.parse("content://sms/inbox");
                        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);
                        int i = 0;
                        cursor.moveToPrevious();
                        while  (cursor.moveToNext())
                        {
                            final String msgid = cursor.getString(0);
                            final String from = cursor.getString(1);
                            final String message = cursor.getString(3);

                            // Executie volly
                            smsSync(from, message, username, password);

                            if (i == 20) {
                                break;
                            }
                            i++;
                        }
                        cursor.close();
                    }
                });

            }
        }, 5, 5, TimeUnit.MINUTES); // or .MINUTES, .SECONDS ,.HOURS etc.

        // end arekta scedule task

    }// End OnCreate//

    public void startService(View v) {
        String input = "Run Backgroudn Service";

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }


    private void smsSync(String from, String message,String username, String password){

        StringRequest postRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("fuck", response);
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        // Log.d("Error.Response", response);
                        Log.d("fuck", error.toString());
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("from", from);
                params.put("message",message);
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };
        // Add the request to the queue
        Volley.newRequestQueue(this).add(postRequest);

    }

    private void TimerMethod()
    {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);
        int i = 0;
        cursor.moveToPrevious();
        while  (cursor.moveToNext())
        {
            final String msgid = cursor.getString(0);
            final String from = cursor.getString(1);
            final String message = cursor.getString(3);

            // Executie volly
            smsSync(from, message, username, password);
            if (i == 20) {
                break;
            }
            i++;
        }
        cursor.close();
    }



    private void testTimer(){
        Log.d("fuck","Fucking success");
    }




}

//https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
//https://stackoverflow.com/questions/8614211/deleting-android-sms-programmatically