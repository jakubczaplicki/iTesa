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
import java.util.List;

import android.util.Log;

import org.apache.commons.math.stat.*;

public final class DataItem
{
   CsvFileAdapter csvFileAdapter = null;
   private final static List<DataMagnetometer> dataArray = new ArrayList<DataMagnetometer>();

   public DataItem()
   {
   }    
    
   public DataItem(long _time, float _xB, float _yB, float _zB) {
      /*B.t = _time;
        B.x = _xB;
        B.y = _yB;
        B.z = _zB;
        setAvg(size);
        this.dataArray = new ArrayList<magData>();*/
   }

   public void add(long i, long t, float x,float y,float z) {
      DataMagnetometer b = new DataMagnetometer(i, t, x, y, z);
      dataArray.add(b);
      Log.d("iTesa", "add Bn: " + b.n);
   }

   public void logFileOpen() {
      csvFileAdapter = new CsvFileAdapter("iTesa.csv");
      csvFileAdapter.open();
   }

   public void logFileClose() {
      if( csvFileAdapter != null ) {
         if ( csvFileAdapter.isOpen ) {
            csvFileAdapter.close();
         }
      }
   }

   public void logFileSave() {
      DataMagnetometer[] mgArray = DataItem.dataArray.toArray(new DataMagnetometer[DataItem.dataArray.size()]);
      dataArray.clear();
      for (DataMagnetometer magData : mgArray)
      {
         Log.d("iTesa", "save Bn: " + magData.n);
         this.csvFileAdapter.write(magData);
      }
   }
}
