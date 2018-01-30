package com.cehome.task.client;

import jsharp.util.Convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.alibaba.fastjson.JSONObject;

public abstract class TimeTaskPlugin {
	protected static final Logger logger = LoggerFactory.getLogger(TimeTaskPlugin.class);
	 

	public String[][] getConfigDesc() {
		return null;
	}
	 
	public abstract void run(TimeTaskContext context, JSONObject args)  throws Exception;
	public abstract void stop(TimeTaskContext context)  throws Exception;
	
}
