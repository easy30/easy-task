package com.cehome.task.console;

import com.cehome.task.service.MachineBaseService;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;


public abstract class MachineListService extends MachineBaseService implements InitializingBean {


    public  String getRandomMachine(String appName){
        Collection<String> ipSet=getMachines(appName);
        String randomIp=null;
        if(ipSet.size()>0){
            String[] ips= ipSet.toArray(new String[0]);
            int n=new Random().nextInt(ips.length);
            randomIp=ips[n];
        }
        return randomIp;
    }

    public  Collection<String> getMachines(boolean withAppName){
        HashSet<String> set=new LinkedHashSet<>();
        for (String appName : getAppNames()) {
            set.addAll(getMachines(appName,withAppName));
        }
        return set;
    }
    public   Collection<String> getMachines(String appName){
        return getMachines(appName,false);
    }

    //public abstract String getIpByHostName(String hostName);
    public abstract Collection<String> getAppNames();
    public abstract void removeAppName(String appName);

    public abstract Collection<String> getMachines(String appName,boolean withAppName);

    public abstract String getServiceUrl(String appName,String machine);

}
