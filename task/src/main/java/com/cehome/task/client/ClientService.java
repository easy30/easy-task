package com.cehome.task.client;

import java.util.List;

public interface ClientService {

    String status(String p) throws Exception;

    public abstract String getLog(String logName, long pageNo, long pageSize) throws Exception;

	public abstract String getLogSize(String logName) throws Exception;

	 
}