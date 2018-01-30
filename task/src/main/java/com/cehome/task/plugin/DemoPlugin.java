package com.cehome.task.plugin;

import com.alibaba.fastjson.JSONObject;
import com.cehome.task.client.TimeTaskPlugin;
import com.cehome.task.client.TimeTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;

/**
 * Created by coolma on 2017/9/29.
 */
@Component
public class DemoPlugin extends TimeTaskPlugin {
    private static final Logger logger = LoggerFactory.getLogger(DemoPlugin.class);
    @Override
    public void run(TimeTaskContext context, JSONObject args) throws Exception {
        logger.info("task id="+context.getId());
        logger.info("task run on ip="+Inet4Address.getLocalHost().getHostAddress());
        logger.info("task sleep 1000");
        Thread.sleep(1000);
        logger.info("task run cost:"+context.getRunTimes());
    }

    @Override
    public void stop(TimeTaskContext context) throws Exception {

    }
}
