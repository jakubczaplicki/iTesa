/*
 * Copyright (C) 2011 The iTesa Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.funellites.iTesa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener  {
    public final static String TAG = "iTesa";
    protected TextView tBTextView;
    protected TextView nBTextView;
    protected TextView xBTextView;
    protected TextView yBTextView;
    protected TextView zBTextView;
    protected TextView absBTextView;
    protected TextView avgBTextView;
    protected TextView maxBTextView;
    protected TextView iTextView;
    protected CheckBox logData_cb;

    DataMagnetometer Blocal = new DataMagnetometer();
    GraphView graphView = null;

   static final private int MENU_PREFERENCES = Menu.FIRST; 
   private static final int SHOW_PREFERENCES = 1;
   private int updateFreq   = 3000; // ms
   private int saveLogFreq  = 1000; // ms
   private boolean logData  = false;
   // private DBAdapter      dbAdapter      = null; // TODO: database

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d("iTesa",this.getClass().getName()+":onCreate()");
      setContentView(R.layout.main);

      updateFromPreferences(); // load and set preferences

      // dbAdapter      = new DBAdapter(this); // TODO: database

      tBTextView   = (TextView) findViewById(R.id.tB);
      nBTextView   = (TextView) findViewById(R.id.nB);
      xBTextView   = (TextView) findViewById(R.id.xB);
      yBTextView   = (TextView) findViewById(R.id.yB);
      zBTextView   = (TextView) findViewById(R.id.zB);
      absBTextView = (TextView) findViewById(R.id.absB);
      maxBTextView = (TextView) findViewById(R.id.maxB);
      avgBTextView = (TextView) findViewById(R.id.avgB);
      iTextView    = (TextView) findViewById(R.id.iB);
      graphView    = (GraphView)this.findViewById(R.id.XYPlot);
      logData_cb   = (CheckBox) findViewById(R.id.logData_cb);
      logData_cb.setOnClickListener(this);
        
      //makeThread(); // start a thread to refresh UI

      getWindow().addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
    }
    
   public void onClick(View src) {
       switch (src.getId()) {
       case R.id.logData_cb:
           if (!logData_cb.isChecked()) {
               stopService(new Intent(this, LogService.class));
           }
           else
           {
              // dbAdapter.open();      // TODO: database
              startService(new Intent(this, LogService.class));
              Toast.makeText(getBaseContext(), "Logging state changed", Toast.LENGTH_SHORT).show();
           }
           break;
       }
   }
   
   @Override
   protected void onResume() {
      super.onResume();
       Log.d("iTesa", "MainActivity:onResume()");
       updateFromPreferences();
   }

   @Override
   protected void onPause() {
      super.onPause();
      Log.d("iTesa", "MainActivity:onPause()");
   }

   @Override
   protected void onStop() {
      super.onStop();
      Log.d("iTesa", "MainActivity:onStop()");
   }
    
   @Override
   protected void onDestroy() {
      super.onDestroy();
      Log.d("iTesa", "MainActivity:onDestroy()");
      // stop the thread. Do not use unsafe depreciated guiThread.stop(); 
      // see : http://stackoverflow.com/questions/4756862/how-to-stop-a-thread )
      /*
      guiThread.threadRunning  = false;
      try {
         guiThread.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      logThread.threadRunning  = false;
      try {
         logThread.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      */

      // close the database 
      /*if ( dbAdapter.isOpen ) {
        dbAdapter.close(); // TODO: database
      }*/
   }
    
   /** Updates the text fields on the UI. */
   private void updateGUI() {
      tBTextView.setText("t: " + Blocal.t/1000000 + " ms  ");
      nBTextView.setText("n: " + Blocal.n);
      xBTextView.setText("x: " + Blocal.x + " μT");
      yBTextView.setText("y: " + Blocal.y + " μT");
      zBTextView.setText("z: " + Blocal.z + " μT");
      absBTextView.setText("abs: " + Blocal.abs + " μT");
      maxBTextView.setText("max: " + Blocal.max + " μT");
      //avgBTextView.setText("avg: " + Blocal.sma + " μT");
      // I don't understand this number !! why is it ~53 ms ?
      // see http://stackoverflow.com/questions/5060628/android-sensor-delay-fastest-isnt-fast-enough
      //iTextView.setText("smpl.rate: " + magnetometer.delay + " ms");

      tBTextView.invalidate();
      xBTextView.invalidate();
      yBTextView.invalidate();
      zBTextView.invalidate();
      absBTextView.invalidate();
      avgBTextView.invalidate();
      maxBTextView.invalidate();
      //iTextView.invalidate();
   }

   /** Updates the graph on the UI. */
   private void updateGraph() {
      graphView.updateGraph( Blocal.abs );
   }

/*************************************************************************
 * Thread functionality
 *************************************************************************/

   private GuiThread guiThread;

   /** Creates the thread (this method is invoked from onCreate()) */
   private void makeThread() {
      Log.d("iTesa", "makeThread()");
      guiThread = new GuiThread();
      guiThread.start();
   }

   /** Instantiating Handler associated with the GUI thread */
   private Handler msgGuiHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
      //Log.d("iTesa", "msgGuiHandler " + System.currentTimeMillis() );
         switch(msg.what) {
            case 1: updateGUI();        break;
            case 2: updateGraph();      break;
         }
      }
   };

   /** Thread class for refreshing the UI */
   class GuiThread extends Thread {
      public boolean threadRunning = true;

      public GuiThread() {}

      @Override
      public void run() {
    	 Looper.prepare();
         while (threadRunning) {
            //Send update to the main thread
            msgGuiHandler.sendMessage(Message.obtain(msgGuiHandler, 1));
            msgGuiHandler.sendMessage(Message.obtain(msgGuiHandler, 2));
            try {
               Thread.sleep(updateFreq);
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
         Looper.loop();
      }
   }
    
/*************************************************************************
 * Menu and preferences functionality
 *************************************************************************/

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
      return true;
   };

   public boolean onOptionsItemSelected(MenuItem item) {
      super.onOptionsItemSelected(item);

      switch (item.getItemId()) {
         case (MENU_PREFERENCES): {
            Intent i = new Intent(this, Preferences.class);
            startActivityForResult(i, SHOW_PREFERENCES);
            return true;
         }
      }
      return false;
   };

   private void updateFromPreferences() {
      Context context = getApplicationContext();
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      updateFreq = Integer.parseInt(prefs.getString(Preferences.PREF_UPDATE_FREQ, "1000"));
      saveLogFreq  = Integer.parseInt(prefs.getString(Preferences.PREF_STORE_FREQ, "500"));
      // TODO: use the storeFreq param in the timer - requires restarting timer
   };
}
