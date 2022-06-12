package com.example.smartvest;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class dbBlue implements Serializable {
    String btn ="";
    String dust ="";
    String co="";
    String hum="";
    String  temp="";
    public dbBlue(){}
    public dbBlue( String btn, String dust, String co, String hum, String temp){
        setBtn(btn);
        setDust(dust);
        setCo(co);
        setHum(hum);
        setTemp(temp);

    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setHum(String hum) {
        this.hum=hum;
    }

    public void setCo(String co) {
        this.co=co;
    }

    public void setDust(String dust) {
        this.dust=dust;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }

    @NonNull
    @Override
    public String toString() {
        return "dbBlue[ dust=" +dust +", co=" +co +",hum="+hum +",temp="+temp+"]";
    }

}
