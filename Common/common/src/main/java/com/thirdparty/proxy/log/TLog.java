package com.thirdparty.proxy.log;

import com.orhanobut.logger.Logger;

public class TLog {
	public static boolean DEBUG = true;

	public static void init(String tag,boolean debug){
		TLog.DEBUG = debug;
		Logger.init(tag);
	}


	public static final void e(String log) {
		if (DEBUG)
			Logger.e(log);
	}

	public static final void i(String log) {
		if (DEBUG)
			Logger.i(log);
	}

	public static final void v(String log) {
		if (DEBUG)
			Logger.v(log);
	}

	public static final void w(String log) {
		if (DEBUG)
			Logger.w(log);
	}

	public static final void exception(Exception e){
		if(DEBUG)
			e.printStackTrace();
	}

}
