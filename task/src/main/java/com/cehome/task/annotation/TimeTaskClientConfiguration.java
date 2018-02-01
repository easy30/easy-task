package com.cehome.task.annotation;

import com.cehome.task.TimeTaskClient;
import com.cehome.task.TimeTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by coolma
 *
 */
@Configuration
public class TimeTaskClientConfiguration {
    //public static final String LOG_ROOT_PATH = "/data/logs";
    //public static final String LOG_ENCODING="UTF-8";


    @Bean
    public TimeTaskClient createClientTimeTask(){
        return new TimeTaskClient();
        /*TimeTaskClient timeTaskClient =
        //String logPath=DefaultConfigClient.getProperty("task.log.path",LOG_ROOT_PATH+"/"+DefaultConfigClient.getProjectName());
        String path=logPath;
        if(addAppNameToPath){
            if(!path.endsWith("/") && !path.endsWith("\\")){
                path+="/";
            }
            path+=timeTaskFactory.getAppName();


        }
        timeTaskClient.setLogPath(path);
        timeTaskClient.setLogEncoding(logEncoding);
        timeTaskClient.setLogPackages(logPackages);
        //use hostname
        timeTaskClient.setUseHostName(useHostName);
        timeTaskClient.setHeartBeatSendInterval(heartBeatSendInterval);
        timeTaskClient.setTaskCheckInterval(taskCheckInterval);
        return timeTaskClient;*/
    }

}
