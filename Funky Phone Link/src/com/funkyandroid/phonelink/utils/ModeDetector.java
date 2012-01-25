package com.funkyandroid.phonelink.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class ModeDetector {

	public static final boolean isDiallingPossible(final Context context) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);

		List<ResolveInfo> handlers =
				context.getPackageManager().queryIntentActivities(callIntent, PackageManager.MATCH_DEFAULT_ONLY);
		for(ResolveInfo handler : handlers) {
			if(!handler.activityInfo.packageName.startsWith(context.getPackageName())) {
				return true;
			}
		}

		return false;
	}
}
