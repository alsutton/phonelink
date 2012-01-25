package com.funkyandroid.phonelink.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

import com.funkyandroid.phonelink.FunkyPhoneLinkActivity;
import com.funkyandroid.phonelink.IntentHandler;

/**
 * Class to handle a connection from a bluetooth client
 */

class ConnectionHandlerThread extends Thread {

	/**
	 * The highest protocol number this thread can handle
	 */

	private static final int MAX_PROTOCOL_VERSION = 2;

	/**
	 * The errors which can be sent back to the client
	 */

	public static final int OK = 0,
							E_GENERAL_ERROR = 1,
							E_UNSUPPORTED_PROTOCOL_VERSION = -2;

	/**
	 * The socket the client connected to.
	 */

	private final BluetoothSocket mSocket;

	/**
	 * The handler for intents.
	 */

	private final IntentHandler mIntentHandler;

	public ConnectionHandlerThread(BluetoothSocket socket, IntentHandler intentHandler) {
		mSocket = socket;
		mIntentHandler = intentHandler;
	}

	@Override
	public void run() {
		int resultCode = E_GENERAL_ERROR;

		// First read the string holding the action to be performed
		try {
			DataInputStream dis = new DataInputStream(mSocket.getInputStream());
			try {
				resultCode = readStream(dis);
			} catch(IOException ioe) {
				Log.w("PhoneLink", "Problem getting data from client", ioe);
			}

			DataOutputStream dos = new DataOutputStream(mSocket.getOutputStream());
			try {
				dos.writeInt(resultCode);
			} catch(IOException ioe) {
				Log.w("PhoneLink", "Problem sending response to client", ioe);
			}
		} catch(IOException ioe) {
			Log.w(FunkyPhoneLinkActivity.LOG_TAG, "Problem getting stream", ioe);
		}
	}

	/**
	 * Handle the data coming over the socket.
	 *
	 * @param dis The input stream to read data from
	 * @return a status code to go back to the client.
	 */

	private int readStream(final DataInputStream dis)
		throws IOException {
		if(dis.readInt() > MAX_PROTOCOL_VERSION) {
			return E_UNSUPPORTED_PROTOCOL_VERSION;
		}

		int length = dis.readInt();
		Log.i("PhoneLink", "Received a "+length+" byte parcel");
		byte[] data = new byte[length];
		int position = 0;
		do {
			position += dis.read(data, position, length-position);
		} while(position < length);

		Parcel parcel = Parcel.obtain();
		parcel.unmarshall(data, 0, length);
		parcel.setDataPosition(0);
		Intent newIntent = (Intent) parcel.readValue(Intent.class.getClassLoader());
		mIntentHandler.processIntent(newIntent);
		return OK;
	}
}
