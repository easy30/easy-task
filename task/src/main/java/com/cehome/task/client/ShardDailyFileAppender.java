package com.cehome.task.client;

import java.util.Map;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
public class ShardDailyFileAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Map<String,Appender> appenderMap=new java.util.concurrent.ConcurrentHashMap <String,Appender>();

	//protected String file;
	///protected String encoding;
	//protected boolean append=true;
    protected static String DEFAULT_NAME= "default";

	protected String fileNamePattern;

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public int getMaxHistory() {
		return maxHistory;
	}

	public void setMaxHistory(int maxHistory) {
		this.maxHistory = maxHistory;
	}

	public FileSize getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(FileSize maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	protected int maxHistory;
	protected FileSize maxFileSize;
	protected String pattern;
	
	private Appender getAppender(String name){
		Appender appender=appenderMap.get(name);
		if(appender==null)
			synchronized(this){
				appender=appenderMap.get(name);	
				if(appender==null) {
					appender=createAppender(name);
					System.out.println("ShardDailyFileAppender.create appender.name="+name);
					appenderMap.put(name, appender);
				}
		}
		return appender;
	}

    protected Appender createAppender(String name){
		 RollingFileAppender<ILoggingEvent> agentAppender = new RollingFileAppender<ILoggingEvent>();
		agentAppender.setName(name);

		//TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();
		SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<ILoggingEvent>();
		policy.setContext(this.getContext());
		policy.setMaxHistory(maxHistory);
		policy.setMaxFileSize(maxFileSize);
		policy.setFileNamePattern(fileNamePattern.replace("{shard}",name));//"/home/lionbule/%d{yyyy-MM-dd}.log");
		//policy.setTotalSizeCap(maxFileSize);
		policy.setParent(agentAppender);
		policy.start();


		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(this.getContext());
		encoder.setPattern(pattern);
		encoder.start();

		agentAppender.setRollingPolicy(policy);
		agentAppender.setEncoder(encoder);
		agentAppender.setContext(this.getContext());
		agentAppender.setPrudent(true); //support that multiple JVMs can safely write to the same file.
		agentAppender.start();


         return agentAppender;
	}

	@Override
	public void stop() {
		 for(Appender appender:appenderMap.values()){
			 appender.stop();
		 }
		 appenderMap.clear();
		super.stop();

	}




	/*public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}*/


	@Override
	protected void append(ILoggingEvent event) {
		String name=MDC.get("shard");
		if(name==null){
			getAppender(DEFAULT_NAME).doAppend(event);
		}else{
			getAppender(name).doAppend(event);
		}
		String name2=MDC.get("shard2");
		if(name2!=null&&!name2.equals(name)){
			getAppender(name2).doAppend(event);
		}
		String name3=MDC.get("shard3");
		if(name3!=null&& !name3.equals(name)&& !name3.equals(name2)){
			getAppender(name3).doAppend(event);
		}
	}


	public static void main(String[] args) {
		 Logger logger = LoggerFactory.getLogger(ShardDailyFileAppender.class);
		//MDC.put("shard","aaa/bb");
		for(int i=0;i<100000;i++)
		logger.info("hello"+i);

	}
}
