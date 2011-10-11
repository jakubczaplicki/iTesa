package com.funellites.iTesa;

public class DataItem {

    long t;
    float x;
    float y;
    float z;
    //Date created;

    public DataItem(long _time, float _xB, float _yB, float _zB) {
        /*this(_xB, _yB, _zB, _time, new Date(java.lang.System.currentTimeMillis()));*/
        t = _time;
        x = _xB;
        y = _yB;
        z = _zB;
    }
}

