package com.funellites.iTesa;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.AsyncTask;

public class DataItemBufferedOutputTask extends
		AsyncTask<DataItem, Integer, Long> {
	protected Long doInBackground(DataItem... dataitems) {
		
		int count = dataitems.length;
		long retVal = (long) 0;

		// TODO: 1. accept an array of DataItems. 2.
		// Move this and accept a filename parameter
		String FILENAME = "iTesaPhoneOutput.dat";
		DataOutputStream dos = null;

		// Open the buffer and write the data
		try {
			dos = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(FILENAME)));

			// Write data to stream:
			for (int i = 0; i < count; i++) {
				dos.writeLong(dataitems[i].t);
				dos.writeFloat(dataitems[i].x);
				dos.writeFloat(dataitems[i].y);
				dos.writeFloat(dataitems[i].z);
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close
			try {
				if (dos != null) {
					dos.flush();
					dos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return retVal;
	}

	protected void onProgressUpdate(Integer... progress) {
		// TODO Progress
	}

	protected void onPostExecute(Long result) {
		// TODO Post Execute
	}
}