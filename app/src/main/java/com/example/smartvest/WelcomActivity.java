package com.example.smartvest;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class WelcomActivity extends AppCompatActivity {
    TextView logout_worker;
    ConstraintLayout location_worker;
    ConstraintLayout safety_worker;
    ConstraintLayout manual_worker;
    ConstraintLayout report_worker;
    TextView vest_connection;

    private long backKeyPressedTime = 0;
    private Toast toast;
    private List<UserLocation> userLocationList;
    private List<UserLocation> saveLocationList;
    private BluetoothSPP bt;
    LinearLayout bluetooth_connect;
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String str_datetime = sdfNow.format(date);
    BroadcastReceiver safety_receiver = null;

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String userID= intent.getStringExtra("userID");
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            bt.disconnect();
            bt.stopService();
            toast.cancel();
            toast = Toast.makeText(this,"이용해 주셔서 감사합니다.",Toast.LENGTH_LONG);
            toast.show();

            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response)
                {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            //userLocationList.remove(final i);
                            for(int i = 0; i<saveLocationList.size(); i++)
                            {
                                if(saveLocationList.get(i).getUserID().equals(userID))
                                {
                                    saveLocationList.remove(i);
                                    break;
                                }
                            }
                            // notifyDataSetChanged();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            };
            LocationDeleteRequest LocationdeleteRequest = new LocationDeleteRequest(userID, responseListener);
            RequestQueue queue = Volley.newRequestQueue(WelcomActivity.this);
            queue.add(LocationdeleteRequest);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스 사용 불가가"
                    , Toast.LENGTH_SHORT).show();
        }

        location_worker = findViewById(R.id.location_worker);
        manual_worker = findViewById(R.id.manual_worker);
        safety_worker = findViewById(R.id.safety_worker);

        //로그아웃 버튼, 관리자 맵에서 마커 삭제
        logout_worker = findViewById(R.id.logout_worker);
        logout_worker.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        logout_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.disconnect();
                bt.stopService();
                Toast.makeText(WelcomActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(WelcomActivity.this, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                //userLocationList.remove(final i);
                                for (int i = 0; i < saveLocationList.size(); i++) {
                                    if (saveLocationList.get(i).getUserID().equals(userID)) {
                                        saveLocationList.remove(i);
                                        break;
                                    }
                                }
                                // notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                LocationDeleteRequest LocationdeleteRequest = new LocationDeleteRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(WelcomActivity.this);
                queue.add(LocationdeleteRequest);
            }
        });
        //캘린더
        report_worker = findViewById(R.id.report_worker);
        report_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkerCalendarView.class);
                startActivity(intent);
                intent.putExtra("userID", userID);
            }
        });
        //작업자 현재 위치 보기
        location_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkerMapActivity.class);
                intent.putExtra("userID", userID);
                WelcomActivity.this.startActivity(intent);

            }
        });
        //작업자 매뉴얼
        manual_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkerManualActivity.class);
                startActivity(intent);
            }
        });


        //작업자 안전도 확인
        safety_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkerSafety.class);
                intent.putExtra("userID", userID);
                WelcomActivity.this.startActivity(intent);
            }
        });


        //블루투스 연결
        bluetooth_connect = findViewById(R.id.bluetooth_connect);
        bluetooth_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bt.isBluetoothEnabled()) { //
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                } else {
                    if (!bt.isServiceAvailable()) {
                        bt.setupService();
                        bt.startService(BluetoothState.DEVICE_OTHER);
                    }
                }
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                    vest_connection.setText(getString(R.string.text_bluetooth_connect));
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

                }
            }
        });

        Intent intent2 = new Intent(getApplicationContext(), SafetyService.class);
        startService(intent);
        setBluetoothListener(bt, userID);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setBluetoothListener(BluetoothSPP bt, String userID) {
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext(), "휴대폰의 블루투스를 켜주세요.", Toast.LENGTH_SHORT).show();
        } else {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                //데이터 수신
                @Override
                public void onDataReceived(byte[] data, String message) {
                    String btn = message.substring(0, 1);
                    String dust = message.substring(1, 5);
                    String co = message.substring(5, 10);
                    String hum = message.substring(10, 13);
                    String temp = message.substring(13);
                    dbBlue dbBlue= new dbBlue();
                    dbBlue.setBtn(btn);
                    dbBlue.setCo(co);
                    dbBlue.setDust(dust);
                    dbBlue.setHum(hum);
                    dbBlue.setTemp(temp);
                    String str_btn = btn;
                    String str_dust = dust;
                    String str_co = co;
                    String str_hum = hum;
                    String str_temp = temp;


                    Intent intent = IntentWorker.bluetoothIntent(dbBlue, getApplicationContext(),SafetyService.class );
                    startService(intent);

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    BluetoothRequest BluetoothRequest = new BluetoothRequest(userID, str_datetime, str_btn, str_dust, str_co, str_hum, str_temp, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(WelcomActivity.this);
                    queue.add(BluetoothRequest);
                }

            });
            bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
                public void onDeviceConnected(String name, String address) {
                    String s = getString(R.string.text_vest_connection)+"연결";
                    vest_connection.setText(s);
                    Toast.makeText(getApplicationContext()
                            , "Connected to " + name + "\n" + address
                            , Toast.LENGTH_SHORT).show();
                }

                public void onDeviceDisconnected() { //연결해제
                    String s = getString(R.string.text_bluetooth_connect);
                    vest_connection.setText(s);
                }

                public void onDeviceConnectionFailed() { //연결실패
                    Toast.makeText(getApplicationContext()
                            , "블루투스 연결 실패. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
}



