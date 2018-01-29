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
	 
	protected abstract void doRun(TimeTaskContext context, JSONObject args)  throws Exception;
	public  void run(TimeTaskContext context, JSONObject args){
		try {
			//long id=Convert.toLong(context.get("timeTaskId"));
			//String taskId = (context != null && context.getJobDetail() != null) ? context.getJobDetail().g : "";
			//MDC.put("shard", "task/"+id);
			//JSONObject json = (args == null || args.length() == 0) ? new JSONObject() : JSONObject.parseObject(args);
			doRun(context, args);

		} catch (Throwable t) {
			logger.error("timetask error.", t);
		} finally {
			//MDC.remove("shard");
		}
	}
	
}
