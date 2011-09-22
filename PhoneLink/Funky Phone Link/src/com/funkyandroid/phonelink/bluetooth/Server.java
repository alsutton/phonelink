package com.funkyandroid.phonelink.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.funkyandroid.phonelink.IntentHandler;

public class Server extends Thread {
	/**
	 * The name associated with the bluetooth socket
	 */

	private static final String NAME = "Funky Phone Link";

	/**
	 * The UUID for the server socket
	 */

	public static final UUID MY_UUID = new UUID(0x50484f4e45L, 0x4c494e4bL);

	/**
	 * The server socket for bluetooth connections
	 */

	private final BluetoothServerSocket mmServerSocket;

	/**
	 * The intent handler
	 */

	private final IntentHandler mIntentHandler;

	/**
	 * Initialise
	 * @param adapter
	 */
	public Server(final BluetoothAdapter adapter, IntentHandler intentHandler)
		throws IOException {
       	mmServerSocket = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        mIntentHandler = intentHandler;
    }

    @Override
	public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            if (socket != null) {
            	(new ConnectionHandlerThread(socket, mIntentHandler)).start();
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

}
