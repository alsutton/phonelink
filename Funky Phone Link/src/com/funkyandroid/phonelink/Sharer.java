package com.funkyandroid.phonelink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.funkyandroid.phonelink.bluetooth.Client;

public class Sharer extends Activity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent newIntent = new Intent(this, SendingService.class);
		newIntent.putExtra("intent", getIntent());
		super.startService(newIntent);
		finish();
	}


	private class Sender extends AsyncTask<Intent, Void, Exception> {

		@Override

		public void onPostExecute(Exception ex) {
			if( ex == null ) {
				Sharer.this.finish();
				return;
			}

			new AlertDialog.Builder(Sharer.this)
				.setTitle("Error")
				.setMessage(ex.getMessage())
				.setPositiveButton("OK", new OnClickListener() {
					public void onClick(final DialogInterface d, final int id) {
						Sharer.this.finish();
					}
				})
				.show();
		}

		@Override
		public Exception doInBackground(Intent... intents) {
			try {
				Log.i(FunkyPhoneLinkActivity.LOG_TAG, "Sending "+intents[0].toString()+":::"+intents[0].getExtras().toString());
				Client.getInstance().sendIntent(Sharer.this, intents[0]);
				return null;
			} catch (Exception e) {
				Log.e(FunkyPhoneLinkActivity.LOG_TAG, "Error sharing intent.", e);
				return e;
			}
		}
	}
}
