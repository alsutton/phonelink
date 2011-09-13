package com.funkyandroid.phonelink;

import android.content.Intent;

/**
 * Interface for a class wishing to handle intents
 */
public interface IntentHandler {

	/**
	 * Handle an intent.
	 * @param intent
	 */
	public void processIntent(Intent intent);
}
