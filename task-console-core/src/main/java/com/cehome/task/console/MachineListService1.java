package com.cehome.task.console;

import com.cehome.task.Constants;
import com.cehome.task.TimeTaskClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;


/**
 * 
 * @author ruixiang.mrx
 * local  host
 */
//@Service
public class MachineListService1 extends MachineListService {

    @Autowired
    TimeTaskClient timeTaskClient;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

  /*  public String getIpByHostName(String hostName){
        return timeTaskFactory.getLocalIP();
    }*/



    public  String getRandomMachine(String appName){
        return timeTaskClient.getLocalMachine();

    }


    public Collection<String> getMachines(String appName,boolean withAppName) {
        HashSet<String> set=new HashSet<String>();
        set.add((withAppName?appName+ Constants.MACHINE_SPLIT:"")+timeTaskClient.getLocalMachine());
        return set;

	}

    public   Collection<String> getAppNames(){
        HashSet<String> set=new HashSet<String>();
        set.add( timeTaskFactory.getAppName());
        return set;
    }

    public String getServiceUrl(String appName,String machine){
        return timeTaskClient.getServiceUrl();

    }


    public void removeAppName(String appName){

    }



}
