package com.example.LocationSender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class Authorization extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        final EditText etLogin = (EditText)findViewById(R.id.etLogin);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        final Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setEnabled(false);
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://gps.goldns.ru/auth.php?login="+etLogin.getText()+"&pass="+Data.md5(etPassword.getText().toString()));
                HttpResponse response;
                try {
                    response = httpclient.execute(httpget);
                    /*Log.d(MyActivity.LOG_TAG,Data.md5(etPassword.getText().toString()));
                    Log.d(MyActivity.LOG_TAG,response.getStatusLine().getStatusCode()+"");*/
                    if(response.getStatusLine().getStatusCode() == 200) {
                        //Авторизация пройдена
                        Intent i = new Intent(getApplicationContext(), MyActivity.class);
                        i.putExtra("login",etLogin.getText().toString());
                        i.putExtra("pass",etPassword.getText().toString());
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Неверный логин и/или пароль!",Toast.LENGTH_LONG).show();
                        Log.d(MyActivity.LOG_TAG,"1Неверный пароль!");
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Произошла ошибка, проверьте подключение к Интернет и повторите попытку",Toast.LENGTH_LONG).show();
                }
                btnLogin.setEnabled(true);
            }
        });

    }
}