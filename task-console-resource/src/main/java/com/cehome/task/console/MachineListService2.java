package com.cehome.task.console;

import com.cehome.task.service.MachineBaseService;
import com.cehome.task.service.ConfigService;
import jsharp.util.Convert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


/**
 * 
 * @author ruixiang.mrx
 * 在线机器列表
 */
//@Service
public class MachineListService2 extends MachineListService {
    @Autowired
    protected ConfigService configService;
    @Override
    public void afterPropertiesSet() throws Exception {

    }

/*    public String getIpByHostName(String hostName){
        return configService.hget(getClusterName() + KEY_HOST_IP,hostName);
    }*/


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


    public Collection<String> getMachines(String appName,boolean withAppName) {

        Map<String, String> map=  configService.hgetAll(getClusterName() + KEY_MACHINES+appName);
        Set<String> set=new TreeSet<String>();
        for(Map.Entry<String,String> e :map.entrySet()){
           long  t=Convert.toLong(e.getValue(),0);
           //-- 只显示10分钟以内的机器
            if(System.currentTimeMillis()-t<1000*600){
                //if(appName==null || e.getKey().startsWith(appName))
                if(withAppName){
                    set.add(appName+SPLIT+e.getKey());
                }else{
                    set.add(e.getKey());
                }


            }
        }
        return set;

	}


    public   Collection<String> getAppNames(){
       return   configService.smembers(getClusterName() + KEY_APPS);
    }


    public String getServiceUrl(String appName,String machine){

          return    configService.hget( timeTaskFactory.getName()+ MachineBaseService.KEY_MACHINES_URL+ appName,  machine);

    }


    public void removeAppName(String appName){
        configService.srem(getClusterName() +KEY_APPS,appName);
    }



}
