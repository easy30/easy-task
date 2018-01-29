package com.cehome.task.annotation;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by coolma
 *
 */
@Configuration
@Import({TimeTaskFactoryConfiguration.class,TimeTaskClientConfiguration.class})
public class TimeTaskClientStart {


}
