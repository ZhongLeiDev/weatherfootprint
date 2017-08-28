package com.zl.weatherfootprint.util;

public class WTextUtils {
	
	public static boolean isEmpty(String str) {
		if(str!=null && str.length() > 0){
			return false;
		}
		return true;
	}
	
	public static boolean isArrayEmpty(String[] str) {
		if (str != null && str.length > 0) {
			return false;
		}
		return true;
	}

}
