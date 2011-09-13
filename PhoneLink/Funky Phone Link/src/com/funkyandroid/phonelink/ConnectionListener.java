package com.funkyandroid.phonelink;

/**
 * Interface for objects wishing to act as a listener for connections from
 * other devices
 * @author alsutton
 *
 */
public interface ConnectionListener {

	/**
	 * Activate the listener so it is ready to accept connections
	 *
	 * @intentHandler The handler for any read intents.
	 */

	public void activate(IntentHandler intentHandler);

	/**
	 * Deactivate the listener so it no longer accepts connections.
	 */

	public void deactivate();
}
