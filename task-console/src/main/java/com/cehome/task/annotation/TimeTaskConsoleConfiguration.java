package com.cehome.task.annotation;

import com.cehome.task.console.TimeTaskConsole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by coolma
 *
 */
@Configuration
public class TimeTaskConsoleConfiguration {

    @Bean
    public TimeTaskConsole createConsoleTimeTask(){
        TimeTaskConsole consoleTimeTask =new TimeTaskConsole();
        return consoleTimeTask;
    }

}
