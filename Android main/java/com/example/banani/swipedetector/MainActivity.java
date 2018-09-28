package com.example.banani.swipedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

   Button btnTest;
   TextView tvRes;
   String result;
   HttpGetRequest getRequest = new HttpGetRequest();
   String url;
    public static DecimalFormat DECIMAL_FORMATTER;
    SensorManager sensorManager;
    private float[] mMagnetometerData = new float[3];
    float magnetX = 0;
    float magnetY = 0;
    float magnetZ = 0;
    double magnitude = 0;
    ArrayList xd = new ArrayList<String>();
    ArrayList yd = new ArrayList<String>();
    ArrayList zd = new ArrayList<String>();
    ArrayList strd = new ArrayList<String>();
    String sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTest = findViewById(R.id.button2);
        tvRes = findViewById(R.id.textView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        DECIMAL_FORMATTER = new DecimalFormat("#");
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("sendData",  sendData);
                    url = "http://130.245.75.55:5000/testClass?param=" + sendData ;
                    HttpGetRequest getRequest = new HttpGetRequest();
                    result = getRequest.execute(url).get();
                    if(result!= null){
                        if(result.equals("0") ){
                            tvRes.setText("Horizontal");
                        }
                        else{
                            tvRes.setText("Vertical");
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
            {
                mMagnetometerData = sensorEvent.values.clone();
                magnetX = mMagnetometerData[0];
                magnetY = mMagnetometerData[1];
                magnetZ = mMagnetometerData[2];

                magnitude = Math.sqrt((magnetX * magnetX) + (magnetY * magnetY) + (magnetZ * magnetZ));

                xd.add(DECIMAL_FORMATTER.format(magnetX )) ;
                yd.add(DECIMAL_FORMATTER.format(magnetY )) ;
                zd.add(DECIMAL_FORMATTER.format(magnetZ )) ;
                strd.add(DECIMAL_FORMATTER.format(magnitude )) ;

            }
            break;
            default:
                return;
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            sensorManager.registerListener(MainActivity.this, sensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensorManager.SENSOR_DELAY_FASTEST);

            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            sensorManager.unregisterListener(MainActivity.this);
            Log.i("Values",  zd.toString());
            sendData = zd.toString();
            xd.clear();
            yd.clear();
            zd.clear();
            strd.clear();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 20000;

        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result = "";
            String inputLine;
            try {
                URL myUrl = new URL(stringUrl);
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                connection.disconnect();
                result = stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
}
