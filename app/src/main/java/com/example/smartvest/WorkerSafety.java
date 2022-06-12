package com.example.smartvest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class WorkerSafety extends AppCompatActivity {
    private BluetoothSPP bt;
    ImageView bluetooth_connect;
    TextView dustText =  findViewById(R.id.dustText);
    TextView tempText = findViewById(R.id.tempText);
    TextView humText =  findViewById(R.id.humText);
    TextView coText = findViewById(R.id.coText);
    ImageView back_safety_worker;
    BroadcastReceiver safety_receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_safety);
        Intent intent = getIntent();


        back_safety_worker = findViewById(R.id.back_safety_worker);
        back_safety_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        register_receiver();

    }

    private void register_receiver() {
        this.safety_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //String userID= intent.getStringExtra(IntentWorker.userID);
                String btn = intent.getStringExtra(IntentWorker.BTN);
                String dust= intent.getStringExtra(IntentWorker.DUST);
                String co= intent.getStringExtra(IntentWorker.CO);
                String temp= intent.getStringExtra(IntentWorker.TEMP);
                String hum= intent.getStringExtra(IntentWorker.HUM);
                int bbtn = Integer.parseInt(btn);
                int ddust = Integer.parseInt(dust);
                int cco = Integer.parseInt(co);
                if(bbtn == 1)
                {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(WorkerSafety.this);
                    builder.setMessage("긴급상황입니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
                if(cco >=0 && cco <=20)
                {
                    coText.setTextColor(Color.BLUE);
                    coText.setText("좋음\n"+co.concat("ppm"));
                }else if( ddust>=21 && ddust <=400)
                {   coText.setTextColor(Color.GREEN);
                    coText.setText("보통\n"+co.concat("ppm"));
                }else if( ddust>=401 && ddust <= 800)
                {    coText.setTextColor(Color.parseColor("#FF7F00"));
                    coText.setText("나쁨 \n" +co.concat("ppm"));
                }else if( ddust>=801 )
                {   coText.setTextColor(Color.RED);
                    coText.setText("매우나쁨 \n" +co.concat("ppm"));
                }
                if(ddust >=0 && ddust <=30)
                {
                    dustText.setTextColor(Color.BLUE);
                    dustText.setText("좋음\n"+dust.concat("㎍/㎥"));
                }else if( ddust>=31 && ddust <=80)
                {   dustText.setTextColor(Color.GREEN);
                    dustText.setText("보통\n"+dust.concat("㎍/㎥"));
                }else if( ddust>=81 && ddust <= 150)
                {    dustText.setTextColor(Color.parseColor("#FF7F00"));
                    dustText.setText("나쁨 \n" +dust.concat("㎍/㎥"));
                }else if( ddust>=151 )
                {   dustText.setTextColor(Color.RED);
                    dustText.setText("매우나쁨 \n" +dust.concat("㎍/㎥"));
                }
                tempText.setText(temp.concat(" ℃"));
                humText.setText(hum.concat("%"));
            }
        };
    }

    public void unregister_receiver() {
        if (safety_receiver != null) {
            this.unregisterReceiver(safety_receiver);
            safety_receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister_receiver();
    }

}