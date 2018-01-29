package com.cehome.task.domain;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by coolma on 2017/9/18.
 */
public class BeanConfig {
    private String bean;
    private String method;
    private JSONObject args;
    private String stopMethod;

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject getArgs() {
        return args;
    }

    public void setArgs(JSONObject args) {
        this.args = args;
    }

    public String getStopMethod() {
        return stopMethod;
    }

    public void setStopMethod(String stopMethod) {
        this.stopMethod = stopMethod;
    }
}
