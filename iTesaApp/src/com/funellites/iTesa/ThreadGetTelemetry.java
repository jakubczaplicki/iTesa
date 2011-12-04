package com.funellites.iTesa;

import android.os.AsyncTask;

public class ThreadGetTelemetry extends
		AsyncTask<DataMagnetometer, Integer, Long> {
	
	String FILENAME = "telemetry.csv";

	@Override
	protected Long doInBackground(DataMagnetometer... params) {
	    //csvFileAdapter = new CsvFileAdapter("iTesa", "iTesa.csv");
	    //csvFileAdapter.openReader();

		return null;
	}
	
	protected void onProgressUpdate(Integer... progress) {
		// TODO Progress
	}

	protected void onPostExecute(Long result) {
		// TODO Post Execute
	}
}
