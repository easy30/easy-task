package com.cehome.task.client;

import com.cehome.task.domain.TimeTask;

import java.util.HashMap;

/**
 * Created by coolma on 2017/9/18.
 */
public class TimeTaskContext extends HashMap {
    private TimeTask timeTask;
    private long runTimes;
    private volatile boolean running;
    public long getRunTimes() {
        return runTimes;
    }

    void incRunTimes() {
        this.runTimes ++;
    }



    public long getId() {
        return timeTask.getId();
    }

    public String getTaskConfig(){
        return timeTask.getTaskConfig();
    }

    public String getName(){
        return timeTask.getName();
    }
    public String getCategory1(){
        return timeTask.getCategory1();
    }

     void setTimeTask(TimeTask timeTask) {
        this.timeTask = timeTask;
    }

    public boolean isRunning() {
        return running;
    }

     void setRunning(boolean running) {
        this.running = running;
    }

    public void sleep(long ms){
        int n=0;
        while (isRunning() && n<ms) {
            try {
                Thread.sleep(200);
                n+=200;
            } catch (InterruptedException e) {
            }
        }
    }
}
