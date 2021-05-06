package com.example.magnetometerrawdata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView zValueField;
    private TextView yValueField;
    private TextView xValueField;
    private SensorManager sensorManager;
    private Sensor mag;
    private boolean record;
    public static DecimalFormat DECIMAL_FORMATTER;
    EditText ip;
    EditText posName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        record = false;
        SensorEventListener magnetSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    // get values for each axes X,Y,Z
                    zValueField.setText(Float.toString(event.values[2]));
                    xValueField.setText(Float.toString(event.values[0]));
                    yValueField.setText(Float.toString(event.values[1]));
                    if(record){
                        Context context = getApplicationContext();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url ="http://"+ip.getText()+"/situlearn_db_access/hello.php?db=situlearn&query=INSERT INTO magnetometer (position_name,x,y,z) VALUES (\""+posName.getText()+"\","+Float.toString(event.values[0])+","+Float.toString(event.values[1])+","+Float.toString(event.values[2])+")";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.println(0,"INFO", "data inserted");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.println(0,"ERROR", error.getMessage());
                            }
                        });

// Add the request to the RequestQueue.
                        queue.add(stringRequest);
                    }
                }
                Log.d("MY_APP", event.toString());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        zValueField = (TextView) findViewById(R.id.txt_z);
        xValueField = (TextView) findViewById(R.id.txt_x);
        yValueField = (TextView) findViewById(R.id.txt_y);
        ip = findViewById(R.id.input_IP);
        posName = findViewById(R.id.input_name);
        sensorManager.registerListener(magnetSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 500000);
        final Button button = findViewById(R.id.btn_record);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                record = !record;
                if(record)
                    button.setText("Recording..");
                else
                    button.setText("Not recording");
            }
        });
    }
}