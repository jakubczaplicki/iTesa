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

public class DataItem {

    long t;
    float x;
    float y;
    float z;
    //Date created;

    public DataItem() {
    }
    
    public DataItem(long _time, float _xB, float _yB, float _zB) {
        /*this(_xB, _yB, _zB, _time, new Date(java.lang.System.currentTimeMillis()));*/
        t = _time;
        x = _xB;
        y = _yB;
        z = _zB;
    }
}

