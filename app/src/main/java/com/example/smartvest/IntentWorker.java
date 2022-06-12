package com.example.smartvest;

import android.content.Context;
import android.content.Intent;

public class IntentWorker {
    public static final String BTN ="btn";
    public static final String DUST="dust";
    public static final String CO="co";
    public static final String HUM ="hum";
    public static final String TEMP="temp";

    public static Intent bluetoothIntent(dbBlue dblue, Context context, Class cls){
        Intent intent = new Intent(context, cls);
        intent.setPackage("com.example.smartvest");
        intent.putExtra(BTN, dblue.btn);
        intent.putExtra(DUST, dblue.dust);
        intent.putExtra(CO, dblue.co);
        intent.putExtra(HUM, dblue.hum);
        intent.putExtra(TEMP, dblue.temp);


        return intent;
    }
}
