package com.cehome.task.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.util.FileSize;
import jsharp.util.FileKit;
import org.apache.commons.lang3.StringUtils;
import org.h2.util.New;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class LogWrite {

    static String BASE_LOG_NAME = "com.cehome.task.client";
    static String APPENDER_NAME="easy-task-siftingAppender";
    static int maxHistory = 4;
    static String totalSizeCap = "500mb";
    static String maxFileSize = "100mb";
    static String pattern = "%date %level %c{0}.%method [%file:%line] %msg%n";
    // static String charset = "UTF-8";

    public static void start(String logPackages, final String logPath,final String charset) {

        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        //Logger templateLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.myapp");
        //LoggerContext loggerContext = templateLogger.getLoggerContext();


        AppenderFactory<ILoggingEvent> appenderFactory = new AppenderFactory<ILoggingEvent>() {

            @Override
            public Appender<ILoggingEvent> buildAppender(Context context, String discriminatingValue) throws JoranException {
            /* Create your file appender here, with what ever options you need */

                RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
                fileAppender.setName("fileAppender");

                //TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();

                SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<ILoggingEvent>();
                policy.setContext(loggerContext);
                policy.setMaxHistory(maxHistory);
                policy.setMaxFileSize(FileSize.valueOf(maxFileSize));
                policy.setFileNamePattern(FileKit.addSeparator(logPath) + discriminatingValue + "-%d{yyyy-MM-dd}.%i.log");//"/home/lionbule/%d{yyyy-MM-dd}.log");
                policy.setTotalSizeCap(FileSize.valueOf(totalSizeCap));
                policy.setParent(fileAppender);
                policy.start();


                PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                encoder.setContext(loggerContext);
                encoder.setPattern(pattern);
                encoder.setCharset(Charset.forName(charset));
                encoder.start();

                fileAppender.setRollingPolicy(policy);
                fileAppender.setEncoder(encoder);
                fileAppender.setContext(loggerContext);
                fileAppender.setPrudent(true); //support that multiple JVMs can safely write to the same file.
                //fileAppender.setFile("/data/logs/aa-log/a.log");
                fileAppender.start();

                return fileAppender;
            }
        };

        SiftingAppender siftingAppender = new SiftingAppender();
        MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();
        discriminator.setKey("shard");
        discriminator.setDefaultValue("root");
        discriminator.start();

        siftingAppender.setDiscriminator(discriminator);

        /* set your factory to the sifting appender */
        siftingAppender.setAppenderFactory(appenderFactory);
        siftingAppender.setContext(loggerContext);
        siftingAppender.setName(APPENDER_NAME);
        siftingAppender.start();

        //Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (StringUtils.isBlank(logPackages)) {
            logPackages = Logger.ROOT_LOGGER_NAME;
        }
        logPackages+=";"+BASE_LOG_NAME;

        //add log
        info("logPackages="+logPackages);
        addLogs(logPackages,siftingAppender);

        // monitor log rest
        info("monitor log reset");
        addLogResetListener(logPackages,siftingAppender);
        //monitor log change
        info("monitor log change");
        new AppenderConfigThread( logPackages,siftingAppender,0,30,10).start();


    }

    private static void addLogs(String logPackages,SiftingAppender appender){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (String logPackage : logPackages.split(";")) {
            Logger logbackLogger = loggerContext.getLogger(logPackage);
            if(logbackLogger.getAppender(APPENDER_NAME)==null){
                info("add appender to logger:"+logbackLogger.getName());
                //for root , use system define level
                //if(!Logger.ROOT_LOGGER_NAME.equals(logbackLogger.getName())) {
                    logbackLogger.setLevel(Level.INFO);
                    logbackLogger.setAdditive(false);
               // }
                logbackLogger.addAppender(appender);
            }

        }
    }

    private static void addLogResetListener(final String logPackages,final SiftingAppender appender){
        if(!(LoggerFactory.getILoggerFactory() instanceof LoggerContext)){
            info("Ignore logger hook of "+LoggerFactory.getILoggerFactory() );
            //java.lang.ClassCastException: org.slf4j.helpers.SubstituteLoggerFactory cannot be cast to ch.qos.logback.classic.LoggerContext
            return;
        }
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.addListener(new LoggerContextListener() {
            @Override
            public boolean isResetResistant() {
                return false;
            }

            @Override
            public void onStart(LoggerContext context) {

            }

            @Override
            public void onReset(LoggerContext context) {
                //when rest happend ,logger may rest ,delay 20s
                info("log onReset, start to check logs");
                new AppenderConfigThread( logPackages, appender,20,0,1).start();
            }

            @Override
            public void onStop(LoggerContext context) {
                info("logcontext stop");
            }

            @Override
            public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {

            }
        });
    }
    private static void info(String message) {
        System.out.println(new Date().toLocaleString()+" [TASK-INFO] "+message);
    }

    static class AppenderConfigThread extends Thread {

        private String logPackages;
        private int delaySeconds;
        private SiftingAppender  appender;
        private int interval;
        private int times;
        private int currentTimes=0;

        public AppenderConfigThread(String logPackages,SiftingAppender appender, int delaySeconds,int interval,int times) {

            this.logPackages = logPackages;
            this.appender=appender;
            this.delaySeconds=delaySeconds;
            this.interval=interval;
            this.times=times;
            this.setDaemon(true);


        }


        @Override
        public void run() {

            try {
                Thread.sleep(1000*delaySeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            info("auto check loggers appender for " + logPackages+" for "+times);
            while (currentTimes<times) {
                synchronized (AppenderConfigThread.class) {
                    addLogs(logPackages,appender);
                }

                currentTimes++;

                if(interval>0) {
                    try {
                        Thread.sleep(1000 * interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


    }

    public static void main(String[] args) {
        start("com.cehome.task.client", "/data/logs/aa-log/", "UTF-8");
        org.slf4j.Logger logger = LoggerFactory.getLogger(LogWrite.class);
        MDC.put("shard", "ma1");
        logger.info("abc");
        MDC.put("shard", "ma2");
        logger.info("hello");

    }

}



