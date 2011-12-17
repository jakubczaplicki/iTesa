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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener  {
    public final static String TAG = "iTesa";
    protected ImageView imageView;
    protected CheckBox startService;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,this.getClass().getName()+":onCreate()");
        setContentView(R.layout.main);

        //imageView    = (ImageView) findViewById(R.id.image);
        startService   = (CheckBox) findViewById(R.id.startService);
        startService.setOnClickListener(this);
        
        //makeThread(); // start a thread to refresh UI

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
    }
    
    public void onClick(View src) 
    {
        switch (src.getId()) 
        {
            case R.id.startService:
                if (!startService.isChecked()) 
                {
                	// FIXME: doesn't seem to stop the service (notification still on)
                    stopService(new Intent(this, MainService.class));
                    Toast.makeText(getBaseContext(), "Stoping service", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startService(new Intent(this, MainService.class));
                    Toast.makeText(getBaseContext(), "Starting service", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        Log.d("iTesa", "MainActivity:onResume()");
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
    }
    
/*************************************************************************
 * Thread functionality
 *************************************************************************/

    private GuiThread guiThread;

    /** Creates the thread (this method is invoked from onCreate()) */
    private void makeThread()
    {
        Log.d("iTesa", "makeThread()");
        guiThread = new GuiThread();
        guiThread.start();
    }

    /** Thread class for refreshing the UI */
    class GuiThread extends Thread 
    {
        public boolean threadRunning = true;
        public GuiThread() {}

        @Override
        public void run() 
        {
            int n=1;
    	    while (true) 
            {
          	    if (!threadRunning)
          	    {
      		        return;
                }

          	    try
                {
                    Thread.sleep(50);
                    n++;
                } catch(Exception e) { e.printStackTrace(); }

                if (n==40) //  40 * 50 ms = 2000 ms = 2 sec
                {
              	    n=1;
              	    // TODO: update the background image with the most recent atlas image 
             	    /*String path = Environment.getExternalStorageDirectory().toString();
            	    String fname = path+"/atlas.png";*/
                }
            }
        }
    }
}
