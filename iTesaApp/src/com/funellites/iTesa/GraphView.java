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

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/** Plot viewer loosely based on LunarLanding sample app */
/* TODO : Should it be a separate thread just like in the
 *        LunarLanding example ?*/
public class GraphView extends View {
   private Paint absPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
   private ArrayList<Path> pathArray = new ArrayList<Path>(); // array of points to plot
   private Path path;                // http://developer.android.com/reference/android/graphics/Path.html
   private Bitmap mBackgroundImage;  // background image
   private int h = 100, w = 100;     // initial view width and height
   private int i = 5, n = 10, m = 0; // variables for point plotting functionality
    
   public GraphView(Context context) {
      super(context);
      initGraphView();
   }

   public GraphView(Context context, AttributeSet attrs) {
      super(context, attrs);
      initGraphView();
   }

   public GraphView(Context context, AttributeSet ats, int defaultStyle) {
      super(context, ats, defaultStyle);
      initGraphView();
   }
    
/**
 * Initialise the view
 */
   private void initGraphView() {
   Log.d("iTesa", "GraphView:initGraphView()");
   setFocusable(true);
   Resources r = this.getResources();

   //absPaint.setColor(r.getColor(R.color.violet));
   absPaint.setColor(Color.MAGENTA);

   // load background image as a Bitmap instead of a Drawable b/c
   // we don't need to transform it and it's faster to draw this way
   mBackgroundImage = BitmapFactory.decodeResource(r, R.drawable.grav);
   }

   public void destroy() {
      if (mBackgroundImage != null) {
          mBackgroundImage.recycle();
      }
   }
    
   private boolean firstRun = true;
   @Override
   protected void onDraw(Canvas canvas) {
       // I know it's not the best solution, but I don't know when to get view size. 
       // Putting getWidth and getHeight inside initGraphView() returns zero.
       // Width and height are not defined until the view is actually rendered to the screen.
       // But when does it happen ?
       if (firstRun) {    
         h = getHeight();
         w = (int)(((double)getWidth())*0.9d);
         mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, w, h, true);
         firstRun = false;
      }

      // Draw the background image. Operations on the Canvas accumulate
      // so this is like clearing the screen.
      canvas.drawBitmap(mBackgroundImage, 0, 0, null);    

      for (Path path : pathArray) {
         canvas.drawPath(path, absPaint);
      }
      // canvas.drawColor(Color.RED); // debug
      // Log.d("iTesa", "GraphView:onDraw(), h: " + h + " w: " + w); //debug log
   }
    
   /**
    * Update graph from the MainActivity
    */
   public void updateGraph( float val ) {
      path = new Path();        
      //path.addCircle(n, (int)val+100, 5, Path.Direction.CW);
      //n += i;
      //if ( n >= w - 10 || n <= 10 ) { n = 0; i *= -1; }
      /* quasi-simulation */
      path.addCircle(n, (float) (h*((Math.sin(m+(double)n/100d)+1d)/2d)), 5, Path.Direction.CW);
      n += 1;
      if ( n >= w-10 ) { n = 0; m += 1; }
      if ( m > 10 ) { m = -10; }
      if ( pathArray.size() >= 400 ) {
         pathArray.remove(0);
      }
      pathArray.add(path);
      invalidate();
   }
}
