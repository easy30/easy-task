package com.cehome.task.annotation;

import com.cehome.task.TimeTaskFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by coolma
 *
 */
@Configuration
public class TimeTaskFactoryConfiguration {


    private String driverClassName="com.mysql.jdbc.Driver";
    @Value("${task.factory.name}")
    private String factoryName;

    @Value("${task.factory.appName}")
    private String appName;

    @Value("${task.factory.appEnv:}")
    private String appEnv;

    //@Value("${task.factory.createTable:false}")
    //private boolean createTable;

    @Value("${task.datasource.url}")
    private String url;
    @Value("${task.datasource.username}")
    private String username;
    @Value("${task.datasource.password}")
    private String password;

    @Value("${task.factory.redis.host:}")
    private String redisHost;
    @Value("${task.factory.redis.port:6379}")
    private int redisPort;




    //@Bean
    //@ConfigurationProperties(prefix = "task.datasource")
    DataSource createDataSource(){
        DataSource dataSource=new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }


    @Bean
    public TimeTaskFactory createTimeTaskFactory(){
        TimeTaskFactory timeTaskFactory =new TimeTaskFactory();

        timeTaskFactory.setName(factoryName);
        timeTaskFactory.setAppName(appName);
       /* if(DefaultConfigClient.isProPre()) {
            timeTaskFactory.setAppEnv("pre");
        }*/
        timeTaskFactory.setDataSource(createDataSource());
        if(StringUtils.isNotBlank(appEnv)){
            timeTaskFactory.setAppEnv(appEnv);
        }

        //timeTaskFactory.setClusterMode(false);
        if(StringUtils.isNotBlank(redisHost)) {
            timeTaskFactory.setRedisHost(redisHost);
            timeTaskFactory.setRedisPort(redisPort);
        }

        return timeTaskFactory;
    }




}
