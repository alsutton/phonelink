package com.funkyandroid.phonelink;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	addPreferencesFromResource(R.xml.prefs);

        new DeviceListPopulator().execute((Void[]) null);

    }



    /**
     * Async task to populate the devices list
     */

    class DeviceListPopulator extends AsyncTask<Void, Void, Void> {

    	private List<String> names;
    	private List<String> values;

    	@Override
        protected Void doInBackground(Void... param) {
        	BluetoothAdapter adapter =  BluetoothAdapter.getDefaultAdapter();
        	if(adapter == null) {
        		Log.w("PhoneLink", "No bluetooth adapter found for preferences");
        		return null;
        	}

        	Set<BluetoothDevice> devices = adapter.getBondedDevices();
    		Log.i("PhoneLink", "Found "+devices.size()+" devices in state "+adapter.getState());

        	names = new ArrayList<String>(devices.size()+1);
        	values = new ArrayList<String>(devices.size()+1);
        	names.add(Preferences.this.getString(R.string.none));
        	values.add("");
        	for(BluetoothDevice device : devices) {
        		names.add(device.getName());
        		values.add(device.getAddress());
        		Log.i("PhoneLink", "Found "+device.getName());
        	}

        	return null;
        }

        @Override
		protected void onPostExecute(Void result) {
            ListPreference devicesList = (ListPreference) Preferences.this.findPreference("defaultDevice");
            if( names == null ) {
	        	devicesList.setEntries(R.array.empty_device_array);
	        	devicesList.setEntryValues(R.array.empty_device_values_array);
            } else {
	        	devicesList.setEntries(names.toArray(new String[names.size()]));
	        	devicesList.setEntryValues(values.toArray(new String[values.size()]));
            }
        }
    }
}
