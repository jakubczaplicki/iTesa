package com.funellites.iTesa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

  public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

  SharedPreferences prefs;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.userpreferences);	
  }  
} 
