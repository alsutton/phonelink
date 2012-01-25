package com.funkyandroid.phonelink;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ContactListAdapter extends SimpleCursorAdapter {

	public ContactListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
	}

}
