package com.cehome.task.client;

import java.util.List;
import java.util.Map;

public interface ClientService {

    String status(String p) throws Exception;

    public  String getLog(String logName, long pageNo, long pageSize) throws Exception;

	public  String getLogSize(String logName) throws Exception;

    public String httpPost(Map<String,String> params, String path) throws Exception;
    public String httpGet( String path) throws Exception;

	 
}