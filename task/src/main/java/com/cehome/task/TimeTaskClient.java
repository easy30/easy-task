package com.cehome.task;

import com.cehome.task.client.*;
import com.cehome.task.service.MachineBaseService;
import com.cehome.task.util.TimeTaskUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.WebApplicationContext;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class TimeTaskClient implements ApplicationContextAware,BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    protected static final Logger logger = LoggerFactory.getLogger(TimeTaskClient.class);
    private String logPackages;
    private String logPath;
    private String logEncoding="UTF-8";
    private long checkTaskInterval =5 * 1000;

    private boolean useHostName=false;

    protected  String localMachine;
    private String serviceUrl;

    @Autowired
    TimeTaskFactory timeTaskFactory;

    public String getLogPackages() {
        return logPackages;
    }

    public void setLogPackages(String logPackages) {
        this.logPackages = logPackages;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getLogEncoding() {
        return logEncoding;
    }

    public void setLogEncoding(String logEncoding) {
        this.logEncoding = logEncoding;
    }

    public long getCheckTaskInterval() {
        return checkTaskInterval;
    }

    public void setCheckTaskInterval(long checkTaskInterval) {
        this.checkTaskInterval = checkTaskInterval;
    }

    public boolean isUseHostName() {
        return useHostName;
    }

    public void setUseHostName(boolean useHostName) {
        this.useHostName = useHostName;
    }

    public String getLocalMachine(){
        if(localMachine==null) {
            String env = timeTaskFactory.getAppEnv();
            if (StringUtils.isNotBlank(env)) {
                env += Constants.MACHINE_SPLIT;
            } else {
                env = "";
            }
            if (isUseHostName()) {
                localMachine = env + timeTaskFactory.getLocalHostName();
            } else {
                localMachine = env + timeTaskFactory.getLocalIP();
            }
        }
        return 	localMachine;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DefaultListableBeanFactory factory=(DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        LogWrite.start(logPackages,logPath,logEncoding);


        TimeTaskUtil.registerBean(factory,"taskSchedulerService",clientTaskSchedulerService());

        //RemoteLogService remoteLogService=clientRemoteLogService();

        //TimeTaskUtil.registerBean(factory,"remoteLogService",remoteLogService);

        //TimeTaskUtil.registerBean(factory,"ipRmiServiceExporter",clientIPRmiServiceExporter(remoteLogService));
        // 仅spring boot 用， mvc 需要手动指定
         ClientServiceController clientServiceController=new ClientServiceController();
        TimeTaskUtil.registerBean(factory,"clientServiceController",clientServiceController);

        if(timeTaskFactory.isClusterMode()) {
            logger.info("init machineHeartBeatService ");
            TimeTaskUtil.registerBean(factory, "machineHeartBeatService", clientMachineHeartBeatService());
        }


    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext w= (WebApplicationContext)contextRefreshedEvent.getApplicationContext();
/*
        if(inited) {
            ClientServiceController clientServiceController = new ClientServiceController();

            TimeTaskUtil.registerBean((AbstractAutowireCapableBeanFactory) w.getAutowireCapableBeanFactory(), "clientServiceController", clientServiceController);
            return;
        }else{
            inited=true;
        }*/

        new Thread(new Runnable() {

            @Override
            public void run() {
                long t=System.currentTimeMillis();
                String[] info=null;
                while (System.currentTimeMillis()-t<100*1000) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    info=getHttpInfo();
                    if(info!=null){
                        break;
                    }

                }

                String contextPath=w.getServletContext().getContextPath();
                if(!contextPath.endsWith("/"))contextPath+="/";
                String baseUrl=info[0]+"://" +timeTaskFactory.getLocalIP()+":"+info[1]+ contextPath;
                registerServiceUrl(baseUrl);
            }
        }).start();
    }


    public TimeTaskSchedulerService clientTaskSchedulerService(){
        TimeTaskSchedulerService timeTaskSchedulerService =new  TimeTaskSchedulerService();
        //factory.createBean(  TimeTaskSchedulerService.class);
        timeTaskSchedulerService.setAppName(timeTaskFactory.getAppName());
        timeTaskSchedulerService.setCheckTaskInterval(getCheckTaskInterval());
        return timeTaskSchedulerService;
    }

    public MachineHeartBeatService clientMachineHeartBeatService(){
        MachineHeartBeatService machineHeartBeatService= new MachineHeartBeatService();// factory.createBean(  MachineHeartBeatService.class);
        machineHeartBeatService.setAppName(timeTaskFactory.getAppName());
        //machineHeartBeatService.setUseHostName(useHostName);
        //machineHeartBeatService.setClusterName(timeTaskFactory.getName());
        machineHeartBeatService.setClusterHeartBeatInterval(timeTaskFactory.getClusterHeartBeatInterval());
        return  machineHeartBeatService;
    }


    public RemoteLogService clientRemoteLogService(){
        RemoteLogServiceImpl service=new RemoteLogServiceImpl();
        service.setLogPath(getLogPath());
        service.setLogEncoding(getLogEncoding());
        return service;
    }

/*
    public IPRmiServiceExporter clientIPRmiServiceExporter(RemoteLogService remoteLogService){
        IPRmiServiceExporter ipRmiServiceExporter=new IPRmiServiceExporter();// factory.createBean(  IPRmiServiceExporter.class);
        ipRmiServiceExporter.setServiceName("remoteLogService");
        ipRmiServiceExporter.setService(remoteLogService);
        ipRmiServiceExporter.setServiceInterface(RemoteLogService.class);
        ipRmiServiceExporter.setRegistryPort(Integer.parseInt(timeTaskFactory.getRmiPort()));
        ipRmiServiceExporter.setReplaceExistingBinding(false);
        return ipRmiServiceExporter;
    }*/

    public void registerServiceUrl(String serviceUrl){
        this.serviceUrl=serviceUrl;
        if(timeTaskFactory.isClusterMode()){

            timeTaskFactory.getConfigService().hset( timeTaskFactory.getName()+ MachineBaseService.KEY_MACHINES_URL+ timeTaskFactory.getAppName(),
                    getLocalMachine(), serviceUrl );

        }
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    private String[] getHttpInfo()   {
        //MBeanServer mBeanServer = null;
        try {
            /*if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            }

            if (mBeanServer == null) {
                logger.debug("调用findMBeanServer查询到的结果为null");
                return null ;
            }*/
            ArrayList<MBeanServer> list= MBeanServerFactory.findMBeanServer(null);
            for (MBeanServer mBeanServer : list) {

                Set<ObjectName> names = null;
                try {
                    //spring boot==>Tomcat:type=Connector,port=10001   mvc ==>  Catalina:type=Connector
                    names = mBeanServer.queryNames(new ObjectName("*:type=Connector,*"), null);
                    // names = mBeanServer.queryNames(new ObjectName("*:type=*,*"), null);
                } catch (Exception e) {
                    return null;
                }
                Iterator<ObjectName> it = names.iterator();
                ObjectName oname = null;
                while (it.hasNext()) {
                    oname = (ObjectName) it.next();
                    String protocol = (String) mBeanServer.getAttribute(oname, "protocol");
                    String scheme = (String) mBeanServer.getAttribute(oname, "scheme");
                    Boolean secureValue = (Boolean) mBeanServer.getAttribute(oname, "secure");
                    Boolean SSLEnabled = (Boolean) mBeanServer.getAttribute(oname, "SSLEnabled");
                    if (SSLEnabled != null && SSLEnabled) {// tomcat6开始用SSLEnabled
                        secureValue = true;// SSLEnabled=true但secure未配置的情况
                        scheme = "https";
                    }
                    if (protocol != null && ("HTTP/1.1".equals(protocol) || protocol.contains("http"))) {
                        String[] result = new String[2];
                        result[0] = scheme;
                        result[1] = ((Integer) mBeanServer.getAttribute(oname, "port")).toString();
                        return result;

               /* if (secure && "https".equals(scheme) && secureValue) {
                    return ((Integer)mBeanServer.getAttribute(oname, "port")).toString();
                } else if (!secure && !"https".equals(scheme) && !secureValue) {
                    return ((Integer)mBeanServer.getAttribute(oname, "port")).toString();
                }*/
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;

    }


}
