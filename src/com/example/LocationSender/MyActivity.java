package com.example.LocationSender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class MyActivity extends Activity {
    public static String LOG_TAG = "Mylog";
    public static String DIR_SD = "LocationData";
    public static String FILENAME_SD = "log.txt";
    public static String absolutePath = Environment.getExternalStorageDirectory()+"/"+ MyActivity.DIR_SD+"/"+MyActivity.FILENAME_SD;
    public static String android_id;
    public static String login;
    public static String passHashMD5;
    SendFileService sfService = new SendFileService();
    Intent intentGetLocationService;
    Intent intentSendFileService;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programm);

        //Получаем параметры от формы авторизации
        Intent i = getIntent();
        login = i.getExtras().getString("login");
        passHashMD5 = Data.md5(i.getExtras().getString("pass"));


        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        TextView tvLastCoordinates = (TextView)findViewById(R.id.tvLastCoordinates);
        TextView tvLastCoordinatesTime = (TextView)findViewById(R.id.tvLastCoordinatesTime);
        final RadioButton rbGSM = (RadioButton)findViewById(R.id.rbGSM);
        final RadioButton rbGPS = (RadioButton)findViewById(R.id.rbGPS);
        RadioGroup radiogroup = (RadioGroup)findViewById(R.id.radiogroup);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for(int i=0; i<rg.getChildCount(); i++) {

                    if(R.id.rbGPS == checkedId) {
                        ((EditText)findViewById(R.id.etLocationRefresh)).setText(GetLocationService.minIntervalGPS/1000+"");
                        stopService(intentGetLocationService);
                        GetLocationService.useGPS=true;
                        intentGetLocationService = new Intent(getApplicationContext(), GetLocationService.class);
                        startService(intentGetLocationService);
                        return;
                    }
                    if(R.id.rbGSM == checkedId)
                    {
                        ((EditText)findViewById(R.id.etLocationRefresh)).setText(GetLocationService.minIntervalGSM/1000+"");
                        stopService(intentGetLocationService);
                        GetLocationService.useGPS=false;
                        intentGetLocationService = new Intent(getApplicationContext(), GetLocationService.class);
                        startService(intentGetLocationService);
                        return;
                    }
                }
            }
        });

        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadSendFile t = new ThreadSendFile();
                t.setDaemon(true);
                t.once=true;
                t.start();
            }
        });

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sendFileTimeToSleep = -1;
                int getLocationTimeToSleep = -1;

                EditText etSendFile = (EditText)findViewById(R.id.etSendFile);
                EditText etLocationRefresh = (EditText)findViewById(R.id.etLocationRefresh);

                sendFileTimeToSleep = Integer.parseInt(etSendFile.getText().toString());
                getLocationTimeToSleep = Integer.parseInt(etLocationRefresh.getText().toString());

                if(getLocationTimeToSleep == -1 || sendFileTimeToSleep == -1){
                    Toast.makeText(getApplicationContext(),"Не правильное число",Toast.LENGTH_LONG).show();
                } else {
                    stopService(intentGetLocationService);
                    if(rbGSM.isSelected()) {
                        GetLocationService.minIntervalGSM = getLocationTimeToSleep*1000;
                    }
                    if(rbGPS.isSelected()) {
                        GetLocationService.minIntervalGPS = getLocationTimeToSleep*1000;
                    }
                    intentGetLocationService = new Intent(getApplicationContext(), GetLocationService.class);
                    startService(intentGetLocationService);


                    stopService(intentSendFileService);
                    SendFileService.timeToSleep=sendFileTimeToSleep*60*1000;
                    intentSendFileService = new Intent(getApplicationContext(), SendFileService.class);
                    startService(intentSendFileService);
                }
            }
        });


        GetLocationService.tvLastCoordinates  = tvLastCoordinates;
        GetLocationService.tvLastCoordinatesTime = tvLastCoordinatesTime;
        GetLocationService.minIntervalGSM = 45000;
        intentGetLocationService = new Intent(this, GetLocationService.class);
        startService(intentGetLocationService);

        SendFileService.timeToSleep=5*60*1000;
        intentSendFileService = new Intent(this, SendFileService.class);
        startService(intentSendFileService);
    }

    @Override
    protected void onDestroy(){
        stopService(intentGetLocationService);
        stopService(intentSendFileService);
        super.onDestroy();
    }
}
