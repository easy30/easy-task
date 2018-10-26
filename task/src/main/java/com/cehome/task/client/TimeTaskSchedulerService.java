package com.cehome.task.client;

import com.alibaba.fastjson.JSONObject;
import com.cehome.task.TimeTaskClient;
import com.cehome.task.TimeTaskFactory;
import com.cehome.task.dao.TimeTaskDao;
import com.cehome.task.domain.BeanConfig;
import com.cehome.task.domain.TimeTask;
import com.cehome.task.service.MachineBaseService;
import com.cehome.task.util.CronExpression;
import com.cehome.task.util.IpAddressUtil;
import com.cehome.task.util.TimeTaskUtil;
import jsharp.util.TimeCal;
import jsharp.util.Convert;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by coolma on 2017/9/18.
 * execute task
 * task: id ,name, type ，express,     bean, method stopmehtod
 */
//@Service
public class TimeTaskSchedulerService implements InitializingBean, DisposableBean, ApplicationContextAware , ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(TimeTaskSchedulerService.class);
    ScheduledThreadPoolExecutor[] executors = null;  //创建5个执行线程
    static  int STATUS_RUNNING=1;
    static int STATUS_STOP=0;
    private Date lastLoadTime = new Date(0);
    //--避免某一秒有多个变化，部分没有检测到，多跑几次 >=, 然后才是>
    private long lastLoadTimeCount=0;

    Map<Long,TaskRunnable> taskRunnableMap=new LinkedHashMap<>();
    //private int threadCount=30;
    private int poolCount=3;
    //private Map<Long, Integer> statusMap = new HashMap<Long, Integer>();
    private  ApplicationContext applicationContext;
    private ExecutorService stopMethodExecutor = Executors.newCachedThreadPool();
    private TimeCal timeCal=new TimeCal(3600);
    private TimeCal timeCal2=new TimeCal(120);
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    private String appName;

    @Autowired
    TimeTaskDao timeTaskDao;
    @Autowired
    TimeTaskFactory timeTaskFactory;
    @Autowired
    TimeTaskClient timeTaskClient;

    private volatile boolean inited=false;

    @Override
    public void destroy() throws Exception {
        this.stopMethodExecutor.shutdownNow();
        for( ScheduledThreadPoolExecutor executor:executors){
            executor.shutdownNow();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    private    boolean isRunning(TaskRunnable taskRunnable){
        return taskRunnable != null && taskRunnable.getStatus() == STATUS_RUNNING;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(inited){
            return;
        }
        inited=true;
        if(StringUtils.isBlank(appName)) throw new RuntimeException("appName is blank");
        executors=new ScheduledThreadPoolExecutor[poolCount];
        for(int i=0;i<poolCount;i++) {
            ScheduledThreadPoolExecutor  executor = new ScheduledThreadPoolExecutor(timeTaskClient.getPoolThreadCount());
            executors[i]=executor;
        }

        TimeTaskFactory.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } catch (Exception e) {
                    logger.error("run ",e);
                }
            }
        }, timeTaskClient.getTaskCheckInterval(),timeTaskClient.getTaskCheckInterval());
    }

    class TaskRunnable implements Runnable{
        private TimeTask timeTask;
        private TimeTaskContext timeTaskContext;
        private volatile int status;


        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public TimeTask getTimeTask() {
             return timeTask;
         }

         public void setTimeTask(TimeTask timeTask) {
             this.timeTask = timeTask;
         }



        public TimeTaskContext getTimeTaskContext() {
            return timeTaskContext;
        }



         public ScheduledFuture getScheduledFuture() {
             return scheduledFuture;
         }

         private volatile ScheduledFuture  scheduledFuture;
        public   TaskRunnable(TimeTask timeTask){
            this.timeTask=timeTask;
            this.status=STATUS_RUNNING;
            timeTaskContext=new TimeTaskContext();
            timeTaskContext.setTimeTask(timeTask);
            timeTaskContext.setRunning(true);

        }
         public void setScheduledFuture(ScheduledFuture  scheduledFuture){
             this.scheduledFuture=scheduledFuture;
         }

        @Override
        public void run() {
            long id=timeTask.getId();
            try {

                MDC.put("shard", "task/"+id);
               // context.put("timeTaskId", Long.parseLong(id));
                logger.info("prepare to run task {} ",id);
               // String config = context.getJobDetail().getJobDataMap().getString("config");
                BeanConfig beanConfig = TimeTaskUtil.getJSON(timeTask.getConfig(), BeanConfig.class);
                String beanName=beanConfig.getBean();
                String beanMethod=beanConfig.getMethod();
                JSONObject beanArgs=beanConfig.getArgs();
                Object bean = applicationContext.getBean(beanName);
                if(bean==null){
                    throw new Exception("can not find bean of name :"+beanName);
                }
                if((bean instanceof TimeTaskPlugin) && (StringUtils.isBlank(beanMethod))){
                    beanMethod="run";
                }

                Method[] methods = bean.getClass().getMethods();
                boolean find = false;
                for (Method method : methods) {
                    if (method.getName().equals(beanMethod)) {
                        find = true;
                        if (method.getParameterTypes().length == 1) {
                            method.invoke(bean, beanArgs);
                            break;
                        } else if (method.getParameterTypes().length == 2) {
                            method.invoke(bean, timeTaskContext,beanArgs);
                            break;
                        } else {
                            throw new Exception("任务 " + id
                                    + "  method args must be (JSONObject) or (TimeTaskContext,JSONObject)");
                        }
                    }
                }
                if (!find)  {
                    throw new Exception("任务 " + id + "  can not find method with name:" + beanMethod);
                }


               // String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
               // System.out.println("Now Time : " + time);
            }catch (Throwable t){
                    logger.error("task " + id + " run with exception",t);
            }finally {
                if(!isRunning(this) ) {
                    logger.info("stop running task {} ", id);
                }else {
                    schedulerNextFire(this);
                }
                timeTaskContext.incRunTimes();
                MDC.remove("shard");
            }

        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

   /* @Scheduled(fixedDelay = Constants.CLIENT_CHECK_TASK_INTERVAL)
    public void loadTask() throws Exception {
        load();
    }*/
    protected String getExSql(String fields,long runCount) {
        String oper=runCount<2?">=":">";
        return "select "+fields+" from "+ timeTaskDao.getTableName()+" where {appName}=? and {updateTime}"+oper+" ? order by {updateTime} ";
    }

    private Set<String> addEnv( Set<String> ipAddressSet){
        if(StringUtils.isBlank(timeTaskFactory.getAppEnv())){
            return ipAddressSet;
        }
        String env=timeTaskFactory.getAppEnv();
        Set<String> set=new HashSet<>();
        for (String s:ipAddressSet){
            set.add(MachineBaseService.buildMachine(env,s));
        }
        return set;

    }

    public void load() throws Exception {
        //if (EnvUtil.isOnline())
        //	return;
        //每小时重新检验一遍
        if(timeCal.isTimeUp()>0){
            lastLoadTime = new Date(0);
        }
        final String fields="{id},{name},{updateTime},{targetIp},{status},{priority},{cron},{cronType},{config},{taskConfig},{category1}";
        String sql = getExSql(fields,lastLoadTimeCount);
        List<TimeTask> timeTasks =timeTaskDao.queryList(sql,appName,lastLoadTime);
        if(timeCal2.isTimeUp()>0) {
            for (int i = 0; i < poolCount; i++) {
                logger.info("{} - find timetasks. app_name={}, size={},active tasks={}", i, appName, timeTasks.size(), executors[i].getActiveCount());
            }
        }
        Date maxDate = lastLoadTime;
        int changeCount = 0;
        int matchCount = 0;
        int runCount = 0;
        Set<String> ipAddressSet = addEnv(timeTaskClient.isUseHostName()?IpAddressUtil.getHostNameSet():IpAddressUtil.getIpAddressSet());
        for (TimeTask timeTask : timeTasks) {
            if (timeTask.getUpdateTime().compareTo(lastLoadTime)>=0) {
                changeCount++;
                boolean ipMatched = ipAddressSet.contains(timeTask.getTargetIp());
                TaskRunnable taskRunnable=taskRunnableMap.get(timeTask.getId());
                //Integer status = statusMap.get(timeTask.getId());
                if (ipMatched) {
                    matchCount++;
                    if (timeTask.getStatus()==STATUS_RUNNING) { // 需要启动
                        runCount++;
                        if (isRunning(taskRunnable )) {

                                if(!sameTask(taskRunnable.getTimeTask(),timeTask)){
                                    stopTask(timeTask.getId());
                                    logger.info("Task " + timeTask.getId() + "config changed，stop first");
                                    startTask(timeTask);

                                    logger.info("Task " + timeTask.getId() + " startup");
                                }else{
                                    logger.info("task has run, no change for {} ",timeTask.getId());
                                }


                        } else {

                            startTask(timeTask);

                            logger.info("Task " + timeTask.getId() + " startup");
                        }
                    } else { // 需要停止
                        if (isRunning(taskRunnable )) {
                            stopTask(timeTask.getId());

                            logger.info("Task " + timeTask.getId() + " need to stop, current status: " + timeTask.getStatus() + " config IP["
                                    + timeTask.getTargetIp() + "]");
                        }
                    }

                } else {

                    if (isRunning(taskRunnable )) {
                        stopTask(timeTask.getId());
                    }

                }

                if (maxDate.compareTo(timeTask.getUpdateTime()) < 0) {
                    maxDate = timeTask.getUpdateTime();
                }

            }
        }
        if (changeCount > 0)
            logger.info("match by IP --> task count：" + matchCount + ", active count：" + runCount);
        if(lastLoadTime.compareTo(maxDate)==0){
            lastLoadTimeCount++;

        }else {
            lastLoadTime = maxDate;
            lastLoadTimeCount=0;
        }

    }

    private boolean sameTask(TimeTask timeTask1,TimeTask timeTask2){
        BeanConfig beanConfig1 = TimeTaskUtil.getJSON(timeTask1.getConfig(), BeanConfig.class);
        BeanConfig beanConfig2 = TimeTaskUtil.getJSON(timeTask2.getConfig(), BeanConfig.class);
        return timeTask1.getCronType()==timeTask2.getCronType() &&
                timeTask1.getCron().equals(timeTask2.getCron()) &&
                timeTask1.getConfig().equals(timeTask2.getConfig()) &&
                Objects.equals(timeTask1.getTaskConfig(),timeTask2.getTaskConfig()) ;
    }

    public void startTask(TimeTask timeTask){
        TaskRunnable taskRunnable=taskRunnableMap.get(timeTask.getId());
        if (isRunning(taskRunnable )) {
            logger.warn("alread start , ignore ...");
            return;
        }
        TaskRunnable runnable = new TaskRunnable( timeTask) ;
        schedulerNextFire(runnable)  ;
        taskRunnableMap.put(timeTask.getId(),runnable);


        //executor.scheduleWithFixedDelay(runnable, 2, 3, TimeUnit.SECONDS);

    }
    private void  schedulerNextFire( TaskRunnable runnable){
        if(!isRunning(runnable)) return;
        long delay=0;
        TimeTask timeTask=runnable.getTimeTask();
        if(timeTask.getCronType()==0){
            delay = Convert.toSeconds(timeTask.getCron())*1000;
        }else{
            try {
                CronExpression cronExpression=new CronExpression(timeTask.getCron());
                long time=System.currentTimeMillis();
                Date date=  cronExpression.getNextValidTimeAfter(new Date(time));
                delay=date.getTime()-time;
                if(delay<=0) delay=1000;//very important
                //System.out.println("-------------------delay:"+delay+","+date.toLocaleString());

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        runnable.setStatus(STATUS_RUNNING);
        ScheduledFuture sf= executors[timeTask.getPriority()].schedule(runnable,delay,TimeUnit.MILLISECONDS);
        runnable.setScheduledFuture(sf);
    }




    public void stopTask(long id) {

        TaskRunnable runnable = taskRunnableMap.get(id);
        if (runnable == null) {
            logger.warn("can not find running id={}", id);
            return;
        }
        if (runnable.getStatus() == STATUS_STOP) {
            logger.warn("already in stopping for id={}", id);
            return;
        }
        synchronized (runnable) {
            if (runnable.getStatus() == STATUS_STOP) {
                return;
            }

                runnable.getTimeTaskContext().setRunning(false);
                runStopMethod(runnable);
                TimeTask timeTask = runnable.getTimeTask();
                ScheduledFuture sf = runnable.getScheduledFuture();
                if (sf.isDone() || sf.isCancelled()) {
                    return;
                }

                sf.cancel(true);
                runnable.setStatus(STATUS_STOP);
                taskRunnableMap.remove(id);

        }


    }

    private void runStopMethod(final TaskRunnable runnable){
        try {
            final TimeTask timeTask = runnable.getTimeTask();
            final BeanConfig beanConfig = TimeTaskUtil.getJSON(timeTask.getConfig(), BeanConfig.class);
            String stopMethod = beanConfig.getStopMethod();
            final Object bean = applicationContext.getBean(beanConfig.getBean());
            if (bean != null && (bean instanceof TimeTaskPlugin) && (StringUtils.isBlank(stopMethod))) {
                stopMethod = "stop";
            }
            final String stopMethod2 = stopMethod;
            if (stopMethod != null && stopMethod.trim().length() > 0) {
                FutureTask<String> future = new FutureTask<String>(new Callable<String>() {// 使用Callable接口作为构造参数
                    public String call() {
                        MDC.put("shard", "task/" + timeTask.getId());
                        try {

                            Method method = bean.getClass().getMethod(stopMethod2, TimeTaskContext.class);
                            method.invoke(bean, runnable.getTimeTaskContext());
                        } catch (Exception e) {
                            logger.error("invoke stop method error. " + stopMethod2 + "(TimeTaskContext timeTaskContext)", e);
                        } finally {
                            MDC.remove("shard");
                        }
                        return null;
                    }
                });
                stopMethodExecutor.execute(future);

                // 在这里可以做别的任何事情
                try {
                    future.get(5000, TimeUnit.MILLISECONDS); // 取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
                } catch (Exception e) {
                    logger.error("wait for stop method error", e);
                    future.cancel(true);
                } finally {
                }
            }
        }catch (Exception e){
            logger.error("handle stop method error.", e);
        }

    }


    public static void main(String[] args) throws Exception {

        CronExpression cronExpression=new CronExpression("0/1 * * * * ?");
        Date date=new Date();
        long t=date.getTime();
        while (true){

            if(new Date().compareTo(date)>=0){
                System.out.println("run now:"+(System.currentTimeMillis()-t));
                t=System.currentTimeMillis();
                  date=  cronExpression.getNextValidTimeAfter(new Date());

            }
        }

  /*

        TimeTaskSchedulerService taskService=new TimeTaskSchedulerService();
        taskService.threadCount=5;
        taskService.afterPropertiesSet();
        taskService.startTask(null);;
        System.out.println("Now Time : "  + new Date());*/
    }
}
