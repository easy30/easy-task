package com.cehome.task.annotation;

import com.cehome.task.console.TimeTaskConsole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by coolma
 *
 */
@Configuration
public class TimeTaskConsoleConfiguration {

    @Value("${task.heartBeatFailSwitchTime:30000}")
    private long heartBeatFailSwitchTime;
    @Value("${task.heartBeatCheckInterval:30000}")
    private long heartBeatCheckInterval;

    //-- 容灾切换
    @Value("${task.switchEnable:true}")
    private boolean switchEnable =true;

    @Bean
    public TimeTaskConsole createConsoleTimeTask(){
        TimeTaskConsole consoleTimeTask =new TimeTaskConsole();
        consoleTimeTask.setHeartBeatCheckInterval(heartBeatCheckInterval);
        consoleTimeTask.setHeartBeatFailSwitchTime(heartBeatFailSwitchTime);
        consoleTimeTask.setSwitchEnable(switchEnable);
        return consoleTimeTask;
    }

}
