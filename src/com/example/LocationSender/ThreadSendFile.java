package com.example.LocationSender;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSendFile extends Thread {
    public Boolean once=false;
    int timeToSleep;

    public void SetTimeToSleep(int time){
        timeToSleep = time;
    }

    public int SendFile(String pathTofile) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        int serverResponseCode = 0;
        int maxBufferSize = 1*1024*1024;

        String pathToOurFile = pathTofile;
        String urlServer = "http://gps.goldns.ru/input.php?login="+MyActivity.login+"&pass="+MyActivity.pass+"&id="+MyActivity.android_id;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex) {
            Log.d(MyActivity.LOG_TAG, ex.getMessage());
        }
        return  serverResponseCode;
    }

    @Override
    public void run(){

        while(!isInterrupted()) {
            if(!once) {
                try {
                    sleep(timeToSleep);
                } catch (InterruptedException e) {
                    return;
                }
            }

            int code = SendFile(MyActivity.absolutePath);
            if(code != 0) {
                Log.d(MyActivity.LOG_TAG,"Сервер вернул "+code);
                if(code==200) {
                    Log.d(MyActivity.LOG_TAG,"Удаляем файл");
                    new File(MyActivity.absolutePath).delete();
                }
            }

            if(once) {
                return;
            }
        }

    }
}
