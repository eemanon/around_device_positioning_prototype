package com.example.magnetometerrawdata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.tabs.TabLayout;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private TextView zValueField;
    private TextView yValueField;
    private TextView xValueField;
    private TextView zValueAvgField;
    private TextView yValueAvgField;
    private TextView xValueAvgField;
    private EditText x;
    private EditText y;
    private TextView realx;
    private TextView realy;
    private EditText interval;
    private EditText duration;
    private TabLayout tabLayout;
    private Context context;
    private Button btn_calibrate;
    private SensorManager sensorManager;
    private SensorEventListener magnetSensorListener;
    private Sensor mag;
    private boolean record;
    private CountDownTimer timer;
    private int realX;
    private int realY;
    public static DecimalFormat DECIMAL_FORMATTER;
    private String values;
    EditText ip;
    EditText posName;
    Queue<Float> samplesX;
    Queue<Float> samplesY;
    Queue<Float> samplesZ;
    int bufferLength;
    float[] calibrationValues;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //switch to know when to record data and send it and when only to display new values
        record = false;
        samplesX = new LinkedList<Float>();
        samplesY = new LinkedList<Float>();
        samplesZ = new LinkedList<Float>();
        bufferLength = 10;
        calibrationValues = new float[]{0.0f, 0.0f, 0.0f};
        //init queue
        for(int i = 0;i<bufferLength;i++){
            samplesX.add(0.0f);
            samplesY.add(0.0f);
            samplesZ.add(0.0f);
        }

        values = "INSERT INTO magnetometer (position_name,x,y,z,realX,realY,minterval,mduration,time) VALUES ";

        magnetSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    // get values for each axes X,Y,Z
                    zValueField.setText(Float.toString(event.values[2]));
                    xValueField.setText(Float.toString(event.values[0]));
                    yValueField.setText(Float.toString(event.values[1]));

                    //calc running sum
                    samplesX.remove();
                    samplesY.remove();
                    samplesZ.remove();
                    samplesX.add(event.values[0]);
                    samplesY.add(event.values[1]);
                    samplesZ.add(event.values[2]);

                    xValueAvgField.setText(Float.toString(avg(samplesX)));
                    yValueAvgField.setText(Float.toString(avg(samplesY)));
                    zValueAvgField.setText(Float.toString(avg(samplesZ)));


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

        //init all the gui refs.

        realx = (TextView) findViewById(R.id.txt_realX);
        realy = (TextView) findViewById(R.id.txt_realY);

        zValueField = (TextView) findViewById(R.id.txt_z);
        xValueField = (TextView) findViewById(R.id.txt_x);
        yValueField = (TextView) findViewById(R.id.txt_y);

        zValueAvgField = (TextView) findViewById(R.id.txt_z_avg);
        xValueAvgField = (TextView) findViewById(R.id.txt_x_avg);
        yValueAvgField = (TextView) findViewById(R.id.txt_y_avg);

        duration = (EditText) findViewById(R.id.input_duration);
        interval = (EditText) findViewById(R.id.input_interval);
        x = (EditText) findViewById(R.id.input_x);
        y = (EditText) findViewById(R.id.input_y);

        ip = findViewById(R.id.input_IP);
        posName = findViewById(R.id.input_name);
        context = this;
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("info", "chouette " + tab.getText());
                if(tab.getText().equals("prototype")){
                    Log.i("info", "chouette equals acc");
                    Intent intent = new Intent(context, PrototypeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        final Button button = findViewById(R.id.btn_record);
        final Button buttonRealPos = findViewById(R.id.btn_showrealPos);
        btn_calibrate = findViewById(R.id.btn_calibrate);
        btn_calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrationValues[0]= avg(samplesX);
                calibrationValues[1]= avg(samplesY);
                calibrationValues[2]= avg(samplesZ);
                btn_calibrate.setText("Calibrated");
            }
        });
        sensorManager.registerListener(magnetSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), Integer.parseInt(interval.getText().toString()));

        //get mapping
        ArrayList<Float[]> mapping = importMapping();

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

        buttonRealPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float [] pos = calcPos(mapping, avg(samplesX)-calibrationValues[0], avg(samplesY)-calibrationValues[1]);
                realx.setText(pos[0]+"");
                realy.setText(pos[1]+"");
            }
        });

    }
    ArrayList<Float[]> importMapping(){
        //loads a mapping from real coordinates x [0] y [1] to magnetic field strength values x [2] y [3]
        ArrayList<Float[]> result = new ArrayList<>();
        try {
            Log.i("Info", "test");
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.magnetometer1cm20x33_minusearth)));
            String[] nextLine;
            boolean firstLine=true;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(firstLine){
                    firstLine=false;
                    continue;
                }
                String xReal = nextLine[0].toString();
                String yReal = nextLine[1].toString();
                String xValue = nextLine[2].toString();
                String yValue = nextLine[3].toString();
                Log.i("info", "values: "+xValue + ", "+yValue+", "+xReal+", "+yReal);
                result.add(new Float[] {Float.parseFloat(xReal),Float.parseFloat(yReal), Float.parseFloat(xValue), Float.parseFloat(yValue)});
            }
        } catch (IOException e) {

        }
        Log.i("Info", "imported "+result.size()+ " rows");
        return result;
    }
    private float avg(Queue<Float> samples) {
        float sum = 0.0f;
        for(float number : samples){
            sum+=number;
        }
        return sum/samples.size();
    }
    private float[] calcPos(ArrayList<Float[]> posList, float xt, float yt){
        //lookup: calc distance to each point in list. If distance < previous distance, overwrite previous distance with distance and move on, else move on. At the end return the real coords
        double previous_distance = 10000;
        float[] result = {0f,0f};
        float realX = 0f;
        float realY = 0f;
        for (Float[] f : posList){
            double distance = Math.sqrt((f[2]-xt)*(f[2]-xt)+(f[3]-yt)*(f[3]-yt));
            //Log.i("Debug", "distance: "+distance);
            if (distance<previous_distance){
                previous_distance = distance;
                result[0] = f[0];
                result[1] = f[1];
            }
        }
        return result;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        tabLayout.getTabAt(0).select();
    }
}