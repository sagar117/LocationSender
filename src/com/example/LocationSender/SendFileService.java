package com.example.LocationSender;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


public class SendFileService extends Service {
    public static int timeToSleep;
    public ThreadSendFile t;


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        Log.d("log","Служба отправки файла запущена");
        t =  new ThreadSendFile();
        t.SetTimeToSleep(timeToSleep);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void onDestroy()
    {
        t.interrupt();
        Log.d("log","Служба отправки файла остановлена");
    }
}
