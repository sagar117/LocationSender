package com.example.LocationSender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.Calendar;

public class GetLocationService extends Service {

    static LocationManager locationManager;

    public static TextView tvLastCoordinates = null;
    public static TextView tvLastCoordinatesTime = null;

    public static boolean useGPS = false;
    int minDistance	= 0;        //minimum distance between location updates, in meters
    public static int minIntervalGSM = 45000; //minimum time interval between location updates, in milliseconds
    public static int minIntervalGPS = 10000; //minimum time interval between location updates, in milliseconds

    public void SetIntervalGSM(int interval){
        minIntervalGSM = interval;
    }
    public void SetIntervalGPS(int interval){
        minIntervalGPS = interval;
    }
    public void UseGPS(boolean enableGPS){
        useGPS = enableGPS;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            WriteLocationToFile(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    @Override
    public void onStart(Intent intent, int startid) {


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(useGPS) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minIntervalGPS, minDistance, locationListener);
            Log.d("log","Служба отправки координат запущена каждые"+minIntervalGPS);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minIntervalGSM, minDistance, locationListener);
            Log.d("log","Служба отправки координат запущена каждые"+minIntervalGSM);
        }
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        Log.d(MyActivity.LOG_TAG,"Служба отправки координат остановлена");
    }

    public void WriteLocationToFile(Location location) {

        Calendar calendar = Calendar.getInstance();
        String string = calendar.getTimeInMillis()+":"+location.getLatitude()+":"+location.getLongitude()+":"+location.getAltitude()+":"+location.getAccuracy()+"\n";

        try {
            //предварительно создаём папку
            File f= new File(MyActivity.absolutePath.replace(MyActivity.FILENAME_SD,""));
            f.mkdirs();
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(MyActivity.absolutePath,true));
            // пишем данные
            bw.write(string);
            // закрываем поток
            bw.close();

            tvLastCoordinates.setText(location.getLatitude()+" "+location.getLongitude()+"\nПогрешность: "+location.getAccuracy()+" м.");
            tvLastCoordinatesTime.setText(calendar.getTime().toLocaleString());
            Log.d(MyActivity.LOG_TAG, "Файл записан: " + MyActivity.absolutePath);
        } catch (IOException e) {
            Log.d(MyActivity.LOG_TAG,e.getMessage());
        }
    }
}
