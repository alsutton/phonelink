package com.funkyandroid.phonelink.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.funkyandroid.phonelink.ConnectionListener;
import com.funkyandroid.phonelink.IntentHandler;

public class BluetoothListener
	extends Thread
	implements ConnectionListener {

	/**
	 * The currently active listener
	 */

	private static Server currentlyActive = null;

	/**
	 * Activate the bluetooth listener.
	 */
	public void activate(final IntentHandler intentHandler)
		throws IOException {
		synchronized (BluetoothListener.class) {
			if(currentlyActive != null) {
				return;
			}

			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if(adapter != null) {
				currentlyActive = new Server(adapter, intentHandler);
				currentlyActive.start();
			} else {
				Log.e("PhoneLink", "Bluetooth adapter not found");
			}
		}
	}

	/**
	 * Deactivate the bluetooth listener
	 */

	public void deactivate() {
		synchronized (BluetoothListener.class) {
			if(currentlyActive == null) {
				return;
			}

			currentlyActive.cancel();
		}
	}
}
