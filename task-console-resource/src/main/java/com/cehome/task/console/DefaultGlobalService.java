package com.cehome.task.console;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by coolma on 2017/9/18.
 */
//@Service
public class DefaultGlobalService implements GlobalService {

    @Override
    public long getLoginUserId(HttpServletRequest request){
        return 1;
    }
    @Override
    public String getLoginUsername(HttpServletRequest request){
        return "admin";
    }
    @Override
    public boolean isAdminUser(HttpServletRequest request){
        return true;
    }


}
