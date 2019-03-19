package com.example.wifianalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String ssid,ipAddr,strength,speed,frequency,signal = null;
    TextView details,database;
    SQLiteDatabase db;
    Button checkHistory,analyze,clear,showfile;
    WifiInfo wifiInfo;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=openOrCreateDatabase("WifiDB", Context.MODE_PRIVATE, null);


        db.execSQL("CREATE TABLE IF NOT EXISTS details(ID INTEGER PRIMARY KEY AUTOINCREMENT,ssid VARCHAR, strength VARCHAR, signal VARCHAR, speed VARCHAR, frequency VARCHAR, ipAddr VARCHAR);");

        analyze = (Button) findViewById(R.id.button);
        analyze.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayDetails(v);
            }
        });

        checkHistory = (Button) findViewById(R.id.button2);
        showfile = (Button) findViewById(R.id.button4);

        final Intent i = new Intent(MainActivity.this, Main2Activity.class);
        showfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(i);
            }
        });

        checkHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayDatabase();
            }
        });
    }

    public void displayDatabase() {
        database = (TextView) findViewById(R.id.textView2);
        database.setMovementMethod(new ScrollingMovementMethod());
//
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM details",null);
        if(c.getCount()==0)
        {
            Toast.makeText(this, "Error : No records found", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer buffer=new StringBuffer();
        c.moveToFirst();
        while(c.moveToNext())
        {
              buffer.append("ID: ").append(c.getInt(0)).append("\t\t");
              buffer.append("SSID: ").append(c.getString(1)).append("\t\t");
              buffer.append("Strength: ").append(c.getString(2)).append("dBm\n");
              buffer.append("Signal Strength: ").append(c.getString(3)).append("\n");
              buffer.append("Speed: ").append(c.getString(4)).append("Mbps\n");
              buffer.append("Frequency: ").append(c.getString(5)).append("GHz\n");
              buffer.append("IP Address: ").append(c.getString(6)).append("\n");
        }
        database.setText(buffer);
//
   }

    @SuppressLint("SetTextI18n")
    public void displayDetails(View view) {

        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        details = (TextView) findViewById(R.id.textView);


        int signal_strength = wifiInfo.getRssi();
        //signal = null;
        if (signal_strength > -50) {
            signal = "Excellent";
        } else if (signal_strength < -50 && signal_strength > -60) {
            signal = "Good";
        } else if (signal_strength < -60 && signal_strength > -70) {
            signal = "Fair";
        } else if (signal_strength < -70 && signal_strength > -100) {
            signal = "Weak";
        }

        ssid = wifiInfo.getSSID();
        strength = Integer.toString(wifiInfo.getRssi());
        speed = Integer.toString(wifiInfo.getLinkSpeed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frequency = Float.toString((float) wifiInfo.getFrequency() / 1000);
        }
        ipAddr = Formatter.formatIpAddress(wifiInfo.getIpAddress());

        if (signal!=null) {
            String info = "SSID: " + ssid + "\nStrength: " + strength + "dBm" + "\nSignal Strength: " + signal + "\nSpeed: " + speed + "Mbps" + "\nFrequency: " + frequency + "GHz" + "\nIP Address: " + ipAddr;
            saveFile();
            if(storeInDB(ssid, strength, signal, speed, frequency, ipAddr))
               Toast.makeText(getApplicationContext(), "Successfully Saved",Toast.LENGTH_SHORT).show();
            details.setText(info);
        }
        else{
            details.setText("No WiFi");}
        //db.close();
    }
    public boolean storeInDB(String ssid, String strength, String signal, String speed, String frequency, String ipAddr){
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id",id);
        contentValues.put("ssid",ssid);
        contentValues.put("strength", strength);
        contentValues.put("signal", signal);
        contentValues.put("speed", speed);
        contentValues.put("frequency", frequency);
        contentValues.put("ipAddr", ipAddr);
        db.insert("details", null, contentValues);
        return true;
    }


    public void clearDB(View view) {
        clear = (Button) findViewById(R.id.button3);
        db.execSQL("DELETE FROM details");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM details",null);
        if(c.getCount()==0) {
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        }
        database.setText("");
    }

    public void saveFile()
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WifiAnalyzer");
        dir.mkdirs();
        try {
            Date currentTime = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy::hh:mm:ss-a");
            String formattedDate = df.format(currentTime);
            File myFile = new File(dir, "Signal_Strength_Log.txt");
            if (myFile.length() < 1024000) {
                FileWriter fw = new FileWriter(myFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                String printString = wifiInfo.getSSID() + "\t\t" + formattedDate + "\t\t" + wifiInfo.getRssi() + "\n";
                pw.print(printString);
                pw.close();
                Toast.makeText(this, "Written in File", Toast.LENGTH_SHORT).show();
            }else{
                PrintWriter pw = new PrintWriter(myFile);
                pw.print("");
                pw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

