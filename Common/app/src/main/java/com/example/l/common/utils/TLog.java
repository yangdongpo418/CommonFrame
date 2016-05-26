package com.example.l.common.utils;

import com.orhanobut.logger.Logger;

public class TLog {
	public static boolean DEBUG = true;

	public TLog() {
	}

	public static void init(String tag,boolean debug){
		TLog.DEBUG = debug;
		Logger.init(tag);
	}

	public static final void analytics(String log) {
		if (DEBUG)
			Logger.d(log,DEBUG);
	}

	public static final void e(String log) {
		if (DEBUG)
		Logger.e(log,DEBUG);
	}

	public static final void i(String log) {
		if (DEBUG)
		Logger.i(log,DEBUG);
	}

	public static final void i(String tag, String log) {
		if (DEBUG)
			Logger.i(log,DEBUG);
	}

	public static final void v(String log) {
		if (DEBUG)
			Logger.v(log,DEBUG);
	}

	public static final void w(String log) {
		if (DEBUG)
			Logger.w(log,DEBUG);
	}

	public static final void exception(Exception e){
		if(DEBUG){
			e.printStackTrace();
		}
	}

}
