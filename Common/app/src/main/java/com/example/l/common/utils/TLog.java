package com.example.l.common.utils;

import android.util.Log;

public class TLog {
	public static final String LOG_TAG = "Common";
	public static boolean DEBUG = true;

	public TLog() {
	}

	public static final void analytics(String log) {
		if (DEBUG)
			Log.d(LOG_TAG, log);
	}

	public static final void e(String log) {
		if (DEBUG)
			Log.e(LOG_TAG, "" + log);
	}

	public static final void i(String log) {
		if (DEBUG)
			Log.i(LOG_TAG, log);
	}

	public static final void i(String tag, String log) {
		if (DEBUG)
			Log.i(tag, log);
	}

	public static final void v(String log) {
		if (DEBUG)
			Log.v(LOG_TAG, log);
	}

	public static final void w(String log) {
		if (DEBUG)
			Log.w(LOG_TAG, log);
	}
}
