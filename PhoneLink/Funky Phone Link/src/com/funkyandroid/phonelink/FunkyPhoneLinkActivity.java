package com.funkyandroid.phonelink;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.funkyandroid.phonelink.bluetooth.Client;

public class FunkyPhoneLinkActivity extends Activity {

	/**
	 * The tag for logging entries
	 */

	public static final String LOG_TAG = "PhoneLink";

	/**
	 * The receiver for startup failures from the listener
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
		public void onReceive(final Context context, final Intent intent) {
        	String noBluetoothText = getString(R.string.listener_start_failed);
        	Toast toast = Toast.makeText(context, noBluetoothText, Toast.LENGTH_LONG);
        	toast.show();
    	}
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(BluetoothAdapter.getDefaultAdapter() == null) {
        	String noBluetoothText = getString(R.string.no_bluetooth);
        	Toast toast = Toast.makeText(this, noBluetoothText, Toast.LENGTH_LONG);
        	toast.show();
        	finish();
        	return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ListenerService.LISTENER_START_FAILED_INTENT);
        registerReceiver(receiver, filter);

        startService(new Intent(this, ListenerService.class));

        ((Button) findViewById(R.id.button))
        	.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String number = ((EditText) findViewById(R.id.number)).getText().toString();
					new DiallerThread(number).start();
				}

        	} );

        ((Button) findViewById(R.id.smsButton))
    	.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String number = ((EditText) findViewById(R.id.number)).getText().toString();
				String text = ((EditText) findViewById(R.id.message_text)).getText().toString();
				new SMSThread(number, text).start();
			}

    	} );
    }

    @Override
    protected void onDestroy() {
      unregisterReceiver(receiver);
    }

    @Override
	public void onNewIntent(final Intent intent) {
    	if(Intent.ACTION_CALL.equals(intent.getAction())) {
    		new DiallerThread(intent.getDataString()).start();
    	} else if(Intent.ACTION_DIAL.equals(intent.getAction())) {
    		runOnUiThread(new Runnable() {
    			public void run() {
    				((EditText) findViewById(R.id.number)).setText(intent.getDataString());
    			}
    		});
    	}

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.settings_option:
        	startActivity(new Intent(this, Preferences.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Thread to deal with dialling a number
     */
    private class DiallerThread extends Thread {
    	private final String mNumber;

    	DiallerThread(final String number) {
    		mNumber = number;
    	}

    	@Override
		public void run() {
    		try {
	    		Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+mNumber));
	    		Client.getInstance().sendIntent(FunkyPhoneLinkActivity.this, dialIntent);
    		} catch(Exception ex) {
    			Log.e("PhoneLink", "Problem sending intent", ex);
    		}
    	}
    }

    /**
     * Thread to deal with sending an SMS.
     */
    private class SMSThread extends Thread {
    	private final String mNumber;
    	private final String mMessage;

    	SMSThread(final String number, final String message) {
    		mNumber = number;
    		mMessage = message;
    	}

    	@Override
		public void run() {
    		try {
	    		Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+mNumber));
	    		smsIntent.putExtra("recipients", mNumber);
	    		smsIntent.putExtra("sms_body", mMessage);
	    		Client.getInstance().sendIntent(FunkyPhoneLinkActivity.this, smsIntent);
    		} catch(Exception ex) {
    			Log.e("PhoneLink", "Problem sending intent", ex);
    		}
    	}
    }
}