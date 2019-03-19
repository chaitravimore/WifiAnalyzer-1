package com.example.wifianalyzer;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    TextView display;
//    final static String fileName = "Signal_Strength_Log.txt";
//    final static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"WifiAnalyzer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        display = (TextView) findViewById(R.id.textView3);
        display.setMovementMethod(new ScrollingMovementMethod());
        String line=null;

        Toast.makeText(this, "Reading file", Toast.LENGTH_SHORT).show();
        try {

//            FileReader fr = new FileReader(myFile, true);
//            BufferedReader in = new BufferedReader(new FileReader("Signal_Strength_Log.txt"));
//            StringBuffer buffer = new StringBuffer();
//
//            while((line = in.readLine()) != null)
//            {
//                buffer.append(line);
//            }
//            in.close();
//            display.setText(buffer);
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WiFiAnalyzer");

            FileInputStream fileInputStream = new FileInputStream (new File(dir,"Signal_Strength_Log.txt"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = br.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();
            display.setText(line);

            br.close();
            Toast.makeText(this, "File displayed", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            Toast.makeText(this, "File Not Found Error", Toast.LENGTH_SHORT).show();
        }catch(IOException e) {
            Toast.makeText(this, "IO Error", Toast.LENGTH_SHORT).show();
        }


    }
}
