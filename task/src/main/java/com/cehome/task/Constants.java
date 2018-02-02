package com.cehome.task;


import java.util.Map;

/**
 * Created by coolma on 2017/10/9.
 */
public class Constants {

    public final static String CLIENT_SERVICE_URL_STATUS= "timeTaskClientService/status.htm";
    public final static String CLIENT_SERVICE_URL_GET_LOG= "timeTaskClientService/getLog.htm";
    public final static String CLIENT_SERVICE_URL_GET_LOG_SIZE="timeTaskClientService/getLogSize.htm";

    public static String MACHINE_SPLIT="|";

    public final static String CONFIG_DRIVER="${task.datasource.driverClassName:com.mysql.jdbc.Driver}";
    //public static String RMI_PORT="1189";
    //public static String TABLE = "time_task";


    //Redis服务器IP
    //public static String REDIS_ADDR = DefaultConfigClient.getProperty("redis.cache.server.host");

    //Redis的端口号
    //public static int REDIS_PORT = DefaultConfigClient.getProperty("redis.cache.server.port",6379);

    //public static String CLIENT_LOG_PATH="/home/admin/logs";
    //public static String CLIENT_LOG_ENCODING="UTF-8";
    //public final static long CLIENT_CHECK_TASK_INTERVAL=5 * 1000;
    //public final static long CLIENT_HEART_BEAT_INTERVAL=20 * 1000;


    //public final static long MONITOR_CHECK_INTERVAL=30*1000;
}
