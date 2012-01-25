package com.funkyandroid.phonelink;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.funkyandroid.phonelink.bluetooth.Client;

public class SendingService extends IntentService {

	public SendingService() {
		super("PhoneLink Sending Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Intent realIntent = (Intent) intent.getParcelableExtra("intent");
			Client.getInstance().sendIntent(this, realIntent);
			Toast toast = Toast.makeText(this, R.string.transferred, Toast.LENGTH_LONG);
	    	toast.show();
		} catch(Exception ex) {
			Toast toast = Toast.makeText(this, R.string.transfer_failed, Toast.LENGTH_LONG);
	    	toast.show();
		}
	}

}
