package com.cehome.task.client;

import java.util.List;

public interface RemoteLogService {

	public abstract String getLog(String logName, long pageNo, long pageSize);

	public abstract long getLogSize(String logName);
	List<String[]> list(String path);
	 
}