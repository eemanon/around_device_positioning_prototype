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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView zValueField;
    private TextView yValueField;
    private TextView xValueField;
    private EditText x;
    private EditText y;
    private EditText interval;
    private EditText duration;
    private SensorManager sensorManager;
    private Sensor mag;
    private boolean record;
    private CountDownTimer timer;
    private int realX;
    private int realY;
    public static DecimalFormat DECIMAL_FORMATTER;
    private String values;
    EditText ip;
    EditText posName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        record = false;
        values = "INSERT INTO magnetometer (position_name,x,y,z,realX,realY,minterval,mduration,time) VALUES ";

        SensorEventListener magnetSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    // get values for each axes X,Y,Z
                    zValueField.setText(Float.toString(event.values[2]));
                    xValueField.setText(Float.toString(event.values[0]));
                    yValueField.setText(Float.toString(event.values[1]));
                    if(record){
                        long time = System.currentTimeMillis();
                        Log.d("curtimer: ",time+"");
                        values += "(\""+posName.getText()+"\","+Float.toString(event.values[0])+","+Float.toString(event.values[1])+","+Float.toString(event.values[2])+","+x.getText().toString()+","+y.getText().toString()+","+interval.getText().toString()+","+duration.getText().toString()+","+ time +"),";
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

        duration = (EditText) findViewById(R.id.input_duration);
        interval = (EditText) findViewById(R.id.input_interval);
        x = (EditText) findViewById(R.id.input_x);
        y = (EditText) findViewById(R.id.input_y);

        ip = findViewById(R.id.input_IP);
        posName = findViewById(R.id.input_name);
        sensorManager.registerListener(magnetSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), Integer.parseInt(interval.getText().toString()));
        final Button button = findViewById(R.id.btn_record);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!record) {
                    record = true;
                    timer = new CountDownTimer( Integer.parseInt(duration.getText().toString())*1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            button.setText("Recording for "+ millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            record = false;
                            button.setText("Not recording");
                            y.setText(""+(Integer.parseInt(y.getText().toString())+1));
                            //send request & remove last comma
                            //values =  values.substring(0, values.length() - 1);
                            String url = "http://"+ip.getText()+"/situlearn_db_access/db_access.php";
                            Context context = getApplicationContext();
                            RequestQueue queue = Volley.newRequestQueue(context);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("mytag","inserted data "+response.toString());
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("mytag","error sending data");
                                }
                            }) {
                            @Override
                            protected Map<String, String> getParams()  {
                                Map<String, String> params = new HashMap<>();
                                params.put("db", "situlearn");
                                params.put("query",  values.substring(0, values.length() - 1));
                                Log.d("finalquery", values.substring(0, values.length() - 1));
                                return params;
                            }
                        };
                            queue.add(stringRequest);
                        }
                    }.start();

                }
                else{
                    button.setText("Not recording");
                    values = "INSERT INTO magnetometer (position_name,x,y,z,realX,realY,minterval,mduration, time) VALUES ";
                    timer.cancel();
                    timer = null;
                    record = false;
                }

            }
        });
    }
}