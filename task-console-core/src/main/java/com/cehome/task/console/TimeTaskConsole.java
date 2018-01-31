package com.cehome.task.console;

import com.cehome.task.TimeTaskFactory;
import com.cehome.task.console.controller.DefaultTimeTaskController;
import com.cehome.task.util.TimeTaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TimeTaskConsole implements ApplicationContextAware,BeanPostProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(TimeTaskConsole.class);
    @Autowired
    TimeTaskFactory timeTaskFactory;

    private long heartBeatFailSwitchTime;
    private long heartBeatCheckInterval;

    //-- 容灾切换
    private boolean switchEnable =true;

    public long getHeartBeatFailSwitchTime() {
        return heartBeatFailSwitchTime;
    }

    public void setHeartBeatFailSwitchTime(long heartBeatFailSwitchTime) {
        this.heartBeatFailSwitchTime = heartBeatFailSwitchTime;
    }

    public long getHeartBeatCheckInterval() {
        return heartBeatCheckInterval;
    }

    public void setHeartBeatCheckInterval(long heartBeatCheckInterval) {
        this.heartBeatCheckInterval = heartBeatCheckInterval;
    }

    public boolean isSwitchEnable() {
        return switchEnable;
    }

    public void setSwitchEnable(boolean switchEnable) {
        this.switchEnable = switchEnable;
    }



    protected MachineSwitchService createMachineSwitchService(){
        MachineSwitchService service=new MachineSwitchService();//  factory.createBean(MachineListService.class);//  new MachineListService();
        //service.setClusterName(timeTaskFactory.getName());
        //.setHeartBeatInterval(timeTaskFactory.getClusterHeartBeatInterval());
        //service.setSwitchCheckInterval(getHeartBeatCheckInterval());
        return  service;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DefaultListableBeanFactory factory=(DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        if(timeTaskFactory.isClusterMode()) {
            logger.info("init machineSwitchService");
            TimeTaskUtil.registerBean(factory,"machineListService", new MachineListService2());
            TimeTaskUtil.registerBean(factory,"defaultGlobalService", new DefaultGlobalService());
            TimeTaskUtil.registerBean(factory,"timeTaskService", new TimeTaskService());
            if(isSwitchEnable()) {
                TimeTaskUtil.registerBean(factory, "machineSwitchService", createMachineSwitchService());
            }
        }else{
            TimeTaskUtil.registerBean(factory,"machineListService", new MachineListService1());
            TimeTaskUtil.registerBean(factory,"defaultGlobalService", new DefaultGlobalService());
            TimeTaskUtil.registerBean(factory,"timeTaskService", new TimeTaskService());
        }

        //TimeTaskUtil.registerBean(factory,"defaultTimeTaskController",new DefaultTimeTaskController());





    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
