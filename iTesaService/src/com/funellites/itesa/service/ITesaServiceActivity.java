package com.funellites.itesa.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ITesaServiceActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(this, LogService.class));
    }
}