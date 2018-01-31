package com.cehome.task.console;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by ruixiang.mrx on 2016/5/3.
 */
public class TaskTypeCustom {
    static JSONObject customs;
    static {
       // String path= TaskTypeCustom.class.getName().replace('.','/')+".json";
        try {

          String text=  IOUtils.toString( TaskTypeCustom.class.getClassLoader().getResourceAsStream("time-task-type-custom.json"),"UTF-8");
            customs= JSON.parseObject(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject  getTaskTypeCustom(int area){
        return customs.getJSONObject(""+area);
    }

    public  static void main(String[] args){
        System.out.println(customs);
    }
}
