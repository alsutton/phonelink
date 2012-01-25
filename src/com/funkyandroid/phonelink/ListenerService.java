package com.funkyandroid.phonelink;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.funkyandroid.phonelink.bluetooth.BluetoothListener;

public class ListenerService
	extends Service
	implements IntentHandler {
	/**
	 * The intent broadcast if the listener fails to start
	 */

	public static final String LISTENER_START_FAILED_INTENT = "com.funkyandroid.phonelink.LISTER_STARTUP_FAILED";

	/**
	 * The Bluetooth Listener
	 */

	private final BluetoothListener bluetoothListener = new BluetoothListener();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		try {
			bluetoothListener.activate(this);
		} catch(IOException ex) {
			Log.e(FunkyPhoneLinkActivity.LOG_TAG, "Error starting listener", ex);
			sendBroadcast(new Intent(LISTENER_START_FAILED_INTENT));
		}

		return Service.START_STICKY;
	}

	/**
	 * Handle an intent by dialling
	 */
	public void processIntent(Intent intent) {
		Intent chooseIntent = Intent.createChooser(intent, getText(R.string.app_name));
		chooseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(chooseIntent);
	}
}
