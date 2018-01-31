package com.cehome.task.console;

import com.cehome.task.Constants;
import com.cehome.task.client.ClientService;
import com.cehome.task.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class ClientServiceProxy implements ClientService {
    private static String encoding="UTF-8";
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String baseUrl;
    @Override
    public String status(String p) throws Exception {
        Map<String,String> params=new HashMap<>();
        if(p!=null) {
            params.put("p", "" + p);
        }
        return HttpUtil.httpPost(getBaseUrl()+ Constants.CLIENT_SERVICE_URL_STATUS,params,encoding);
    }

    @Override
    public String getLog(String logName, long pageNo, long pageSize) throws Exception {
        Map<String,String> params=new HashMap<>();
        params.put("logName",""+logName);
        params.put("pageNo",""+pageNo);
        params.put("pageSize",""+pageSize);
        return HttpUtil.httpPost(getBaseUrl()+ Constants.CLIENT_SERVICE_URL_GET_LOG,params,encoding);
    }

    @Override
    public String getLogSize(String logName) throws Exception {
        Map<String,String> params=new HashMap<>();
        params.put("logName",""+logName);
        String res= HttpUtil.httpPost(getBaseUrl()+Constants.CLIENT_SERVICE_URL_GET_LOG_SIZE,params,encoding);
        return res;
    }



}
