package com.funellites.iTesa;

import java.sql.Date;

public class DataItem {

	  float xB;
	  float yB;
	  float zB;
	  long time;
	  Date created;

	  public DataItem(float _xB, float _yB, float _zB, long _time) {
		    this(_xB, _yB, _zB, _time, new Date(java.lang.System.currentTimeMillis()));
		  }
		  
      public DataItem(float _xB, float _yB, float _zB, long _time, Date _created) {
		    xB = _xB;
		    yB = _yB;
		    zB = _zB;
		    time = _time;
		    created = _created; // TODO: do we need this ?
		  }	
}
