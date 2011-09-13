package com.funkyandroid.phonelink;

import android.app.Activity;
import android.content.Intent;
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

import com.funkyandroid.phonelink.bluetooth.Client;

public class FunkyPhoneLinkActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(this, ListenerService.class));

        ((Button) findViewById(R.id.button))
        	.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String number = ((EditText) findViewById(R.id.number)).getText().toString();
					new DiallerThread(number).start();
				}

        	} );
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
}