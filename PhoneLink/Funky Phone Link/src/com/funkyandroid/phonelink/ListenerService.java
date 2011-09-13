package com.funkyandroid.phonelink;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.funkyandroid.phonelink.bluetooth.BluetoothListener;

public class ListenerService
	extends Service
	implements IntentHandler {

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

		bluetoothListener.activate(this);


		return Service.START_STICKY;
	}

	/**
	 * Handle an intent by dialling
	 */
	public void processIntent(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
