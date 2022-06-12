package com.example.smartvest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SafetyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mintent = set(intent);
        sendBroadcast(mintent);

        sendBroadcast(mintent);

        Map<String, Object> request = new HashMap<>();
        request.put("btn", String.valueOf(intent.getStringExtra(IntentWorker.BTN)));
        request.put("dust", String.valueOf(intent.getStringExtra(IntentWorker.DUST)));
        request.put("co", String.valueOf(intent.getStringExtra(IntentWorker.CO)));
        request.put("hum", String.valueOf(intent.getStringExtra(IntentWorker.HUM)));
        request.put("temp", String.valueOf(intent.getStringExtra(IntentWorker.TEMP)));
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public Intent set(Intent intent) {
        intent.putExtra(IntentWorker.BTN, intent.getStringExtra(IntentWorker.BTN));
        intent.putExtra(IntentWorker.DUST, intent.getStringExtra(IntentWorker.DUST));
        intent.putExtra(IntentWorker.CO, intent.getStringExtra(IntentWorker.CO));
        intent.putExtra(IntentWorker.HUM, intent.getStringExtra(IntentWorker.HUM));
        intent.putExtra(IntentWorker.TEMP, intent.getStringExtra(IntentWorker.TEMP));

        return intent;
    }




}
