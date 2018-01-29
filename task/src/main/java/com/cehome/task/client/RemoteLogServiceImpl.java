package com.cehome.task.client;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

public class RemoteLogServiceImpl implements InitializingBean, DisposableBean, RemoteLogService {

	protected static final Logger logger = LoggerFactory.getLogger(RemoteLogServiceImpl.class);
	private String logPath;//= Constants.CLIENT_LOG_PATH;
	private String logEncoding;
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

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void destroy() throws Exception {
	}
	
	@Override
	public List<String[]> list(String path){
		String d =logPath+"/"+path;  
		File dir=new File(d);
		File[] fs=dir.listFiles();
		Set set=new HashSet();
		List<String[]> result=new ArrayList<String[]>();
		for(File f:fs){
			if(f.isDirectory()){
				result.add(new String[]{f.getName(),"-1"});
			}else{
				String name=f.getName();
				if(name.toLowerCase().endsWith(".log")){
					result.add(new String[]{name, getLogSize(path+"/"+name)+""} );
				}
			}
		}
		return result;
		
	}
	 
	@Override
	public String getLog(String logName, long pageNo, long pageSize) {
		String filename =logPath+"/"+logName;  
		LogShow logShow = new LogShow(filename,getLogEncoding());
		java.io.StringWriter w = new java.io.StringWriter((int) pageSize + 10);
		try {
			logShow.displayReverse(w, -1, pageNo, pageSize);
			return w.toString();
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}

	}

 
	@Override
	public long getLogSize(String logName) {
		String filename =logPath+"/"+logName;  
		LogShow logShow = new LogShow(filename, getLogEncoding());
		return logShow.getTotalSize();
	}

 

}
