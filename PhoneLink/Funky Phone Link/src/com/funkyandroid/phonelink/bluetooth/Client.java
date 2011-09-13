package com.funkyandroid.phonelink.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Client {

	/**
	 * The protocol verison in use
	 */

	private static final int PROTOCOL_VERSION = 1;

	/**
	 * Dial a number
	 */

	public void sendIntent(final Context context, final Intent intent)
		throws IOException, NoPairedDevicesException, DeviceNotFoundException, PhoneLinkRemoteException {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String device = prefs.getString("defaultDevice", null);
		sendToDevice(adapter, device, intent);
	}

	/**
	 * Send some data to another bluetooth device
	 */

	public void sendToDevice(final BluetoothAdapter adapter, final String mac, final Intent intent)
		throws IOException, NoPairedDevicesException, DeviceNotFoundException, PhoneLinkRemoteException {
		if(mac == null) {
			throw new NullPointerException();
		}

		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		if (pairedDevices.size() == 0)  {
			throw new NoPairedDevicesException();
		}

	    // Loop through paired devices
	    for (BluetoothDevice device : pairedDevices) {
	    	String deviceAddress = device.getAddress();
	    	if(mac.equals(deviceAddress)) {
	    		sendData(adapter, device, intent);
	    		return;
	    	}
	    }

	    throw new DeviceNotFoundException();
	}

	/**
	 * Send the intent to a specific device
	 */

	private void sendData(final BluetoothAdapter adapter, final BluetoothDevice device, final Intent intent)
		throws IOException, PhoneLinkRemoteException {
		adapter.cancelDiscovery();
		Log.i("PhoneLink", "Connecting to "+device.getName()+" - "+device.getAddress()+" - "+device.getBondState());
		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(Server.MY_UUID);
		try {
			socket.connect();

			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			try {
				dos.writeInt(PROTOCOL_VERSION);
				sendString(dos, intent.getAction());
				sendString(dos, intent.getData().toString());
			} finally {
				dos.close();
			}

			DataInputStream dis = new DataInputStream(socket.getInputStream());
			try {
				int resultCode = dis.readInt();
				if( resultCode != 0 ) {
					throw new PhoneLinkRemoteException(resultCode);
				}
			} finally {
				dis.close();
			}
		} finally {
			try {
				socket.close();
			} catch(IOException e) {
				// Not worried about closing exceptions
			}
		}

	}

	/**
	 * Send a string to the other end of the socket
	 */

	private void sendString(final DataOutputStream dos, final String string)
		throws IOException {
		byte[] data = string.getBytes("UTF-8");
		dos.writeInt(data.length);
		dos.write(data);
	}

	/**
	 * Exception for when no paired devices are available
	 */

	public static class NoPairedDevicesException extends Exception {

		/**
		 *
		 */
		private static final long serialVersionUID = -9007141612556577864L;

	}

	/**
	 * Exception for when a device could not be found
	 */

	public static class DeviceNotFoundException extends Exception {

		/**
		 *
		 */
		private static final long serialVersionUID = -9007141612356577864L;

	}

	/**
	 * Exception to represent an error returned from the server
	 */

	public static class PhoneLinkRemoteException extends Exception {
		/**
		 * Serial ID
		 */
		private static final long serialVersionUID = 4964126476669611492L;

		/**
		 * The errror code from the server.
		 */
		private final int mErrorCode;

		PhoneLinkRemoteException(final int errorCode) {
			mErrorCode = errorCode;
		}

		public int getErrorCode() {
			return mErrorCode;
		}
	}


	/**
	 * Get an instance of the client
	 */

	private static class INSTANCE_HOLDER {
		static final Client instance = new Client();
	}

	public static Client getInstance() {
		return INSTANCE_HOLDER.instance;
	}
}
