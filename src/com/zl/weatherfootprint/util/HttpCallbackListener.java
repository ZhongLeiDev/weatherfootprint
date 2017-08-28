package com.zl.weatherfootprint.util;

public interface HttpCallbackListener {
	
	void onFinished(String response);
	
	void onError(Exception e);

}
