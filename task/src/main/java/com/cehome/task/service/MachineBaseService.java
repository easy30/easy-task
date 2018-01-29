package com.cehome.task.service;


import com.cehome.task.Constants;
import com.cehome.task.TimeTaskClient;
import com.cehome.task.TimeTaskFactory;
import com.cehome.task.dao.TimeTaskDao;
import com.cehome.task.util.IpAddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ruixiang.mrx -- 1、 机器启动，第一次执行，加入启动map中 2、（频率2秒）更新 服务器机器列表。
 *         放入tair的hashmap（ip,时间） 3、（频率3秒）进行分布式加锁，加锁成功的可以进行状态判断。
 *         获取hashmap，判断时间，如果超过5秒则认为宕机 最终形成在线、离线、刚启动
 *         三钟列表；对于离线的转移、删除key；对于刚启动的恢复，并删除key。
 *
 *         如果宕机，先判断一遍，然后则把宕机切任务切换到正常机器。 -- 将来：机器分组，减少tair竞争。 机器属于一个区域
 *
 */
public abstract class MachineBaseService {
	//protected static String localIP;
	//protected static String localHostName;
	protected static String KEY_APPS= "_time_task_apps";
	protected static String KEY_MACHINES = "_time_task_machines_";
	protected static String KEY_MACHINES_START = "_time_task_machines_start_";
	protected static String KEY_LOCK = "_time_task_matchines_lock";
	public static String KEY_MACHINES_URL = "_time_task_machines_url_";

	protected static String KEY_HOST_IP = "_time_task_host_ip";
	//protected static String KEY_PREFIX_TEST="test_";
	//protected static String KEY_PREFIX_PRO="pro_";
	//protected  String keyPrefix;
	public static String SPLIT="|";

 
	
	//public static String SPLIT="|";

	@Resource
	protected TimeTaskDao timeTaskDao;

	//@Autowired
	//protected TimeTaskClient timeTaskClient;

	@Autowired
	protected TimeTaskFactory timeTaskFactory;

	public MachineBaseService() {
		/*if (DefaultConfigClient.isPro()) {
			keyPrefix = KEY_PREFIX_PRO;
		} else  {
			keyPrefix = KEY_PREFIX_TEST;
		}*/

	}


	public String getClusterName() {

		return timeTaskFactory.getName();
		//return clusterName;
	}

	

	public static String[] extractMachine(String info){
		int n=info.indexOf(Constants.MACHINE_SPLIT);
		String[] result=new String[2];
		if(n==-1){
			result[1]=info;
		}else{
			result[0]=info.substring(0,n);
			result[1]=info.substring(n+Constants.MACHINE_SPLIT.length());
		}
		return result;

	}

	public static String buildMachine(String env,String host){
		if(StringUtils.isBlank(env)){
			return host;
		}
		return  env+Constants.MACHINE_SPLIT+host;
	}

	/*public static List<String> extractHosts(String env, List<String> machines){
		List<String> result=new ArrayList<>();
		for(String s:machines){
			String[]  ss= extractMachine(s);
			if(env==null || env.equalsIgnoreCase(ss[0])){
				result.add(ss[1]);
			}
		}
		return result;
	}*/

	public static List<String> filterMachines(String env, List<String> machines){
		List<String> result=new ArrayList<>();
		for(String m:machines){
			String[]  ss= extractMachine(m);
			if(env==null || env.equalsIgnoreCase(ss[0])){
				result.add(m);
			}
		}
		return result;
	}


	/*public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}*/
}

