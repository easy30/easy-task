package com.cehome.task.console;

import com.alibaba.fastjson.JSONObject;
import com.cehome.task.TimeTaskFactory;
import com.cehome.task.client.RemoteLogService;
import com.cehome.task.dao.TimeTaskDao;
import com.cehome.task.domain.TimeTask;
import com.cehome.task.util.TimeTaskUtil;
import jsharp.util.Convert;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;


//@Service
public class TimeTaskService implements InitializingBean, DisposableBean {
	//public static String APP_NAME = GlobalService.TIME_TASK_APP_NAME;
	protected static final Logger logger = LoggerFactory.getLogger(TimeTaskService.class);
	public static int CAT_SIZE = 4;
	@Resource
	TimeTaskDao timetaskDao;
	@Autowired
	private MachineListService machineListService;

	@Autowired
	TimeTaskFactory timeTaskFactory;

	Map<String, Map<String, Set<String>>> propsMaps = new java.util.concurrent.ConcurrentHashMap<String, Map<String, Set<String>>>();
	long catLastModified = 0;
	long dbLastModified = 0;

	public void afterPropertiesSet() throws Exception {

	}

	/**
	 * 添加和修改
	 * 
	 * @param timeTask
	 * @return
	 */
	public int save(TimeTask timeTask) {
		return timetaskDao.save(timeTask);
	}

	/**
	 * 获取任务
	 * 
	 * @param id
	 * @return
	 */
	public TimeTask get(long id) {
		return timetaskDao.get(id);
	}

	/**
	 * 更新状态 0：停止 1：运行 2：删除
	 * 
	 * @param id
	 * @param toStatus
	 */
	public void updateStatus(long id, int toStatus,String operUser) {
		TimeTask timeTask = timetaskDao.createObject();
		timeTask.setId(id);
		timeTask.setStatus(toStatus);
        timeTask.setOperUser(operUser);
		save(timeTask);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @param real
	 *            true真删 false做标记
	 */
	public void delete(long id, boolean real,String operUser) {
		if (real)
			timetaskDao.deleteById(id);
		else
			updateStatus(id, 2,operUser);
	}

	/**
	 * 优先级 9是高危
	 * 
	 * @param id
	 * @param priority
	 */
	public void updatePriority(int id, int priority,String operUser) {

		TimeTask timeTask = timetaskDao.createObject();
		timeTask.setId(id);
		timeTask.setPriority(priority);
        timeTask.setOperUser(operUser);
		save(timeTask);

	}

	public int batchSwitchIP(List<Long> ids, String appName,String ip,String operUser) {
		//String[] ipInfo=ip.split(",");
		String where = " id in (" + Convert.toString(ids, ",", null) + " ) and {appName}=? ";
		TimeTask timeTask = timetaskDao.createObject();
		timeTask.setTargetIp(ip);
		timeTask.setLastTargetIp("");
        timeTask.setOperUser(operUser);
		return timetaskDao.updateByWhere(timeTask, where,appName) ;
	}

	private Object getTimeTaskList(String words, Map<String, Object> params, int state, int pn, int ps, boolean count)
			throws Exception {

		String sql = "select " + (count ? "count(*)" : "*") + " from " + timetaskDao.getTableName() + " ";
		String where = "";
		List<String> wordList = splitWords(words);
		List queryParams = new ArrayList();
		if (wordList != null && wordList.size() > 0) {
			String[] fields = { "name", "category1", "category2", "category3", "category4", "{appName}",
					"{targetIp}","{operUser}","{createUser}" };
			for (String word : wordList) {
				String ww = "";
				for (String field : fields) {
					if (ww.length() > 0)
						ww += " or ";
					ww += field + " like ? ";
					queryParams.add("%" + word + "%");
				}

				where += " and ( " + ww + " ) ";
			}
		}

		if (params.get("category1") != null) {
			where += " and ( category1 = ? ) ";
			queryParams.add(params.get("category1"));
		}
		if (params.get("category2") != null) {
			where += " and ( category2 = ? ) ";
			queryParams.add(params.get("category2"));
		}
		if (params.get("category3") != null) {
			where += " and ( category3 = ? ) ";
			queryParams.add(params.get("category3"));
		}
		if (params.get("category4") != null) {
			where += " and ( category4 = ? ) ";
			queryParams.add(params.get("category4"));
		}
		if (params.get("ip") != null) {
			where += " and ( target_ip = ? ) ";
			queryParams.add(params.get("ip"));
		}

	/*	if (params.get("scheduler") != null) {
			where += " and ( scheduler = ? ) ";
			queryParams.add(params.get("scheduler"));
		}*/

		if (params.get("priority") != null) {
			where += " and ( priority = ? ) ";
			queryParams.add(params.get("priority"));
		}

		if (params.get("appName") != null) {
			where += " and ( {appName} = ? ) ";
			queryParams.add(params.get("appName"));
		}
		int taskType = Convert.toInt(params.get("taskType"), -1);
		if (taskType>-1) {
			where += " and ( {taskType} = ? ) ";
			queryParams.add(params.get("taskType"));
		}

		if (params.get("userId") != null) {
			where += " and ( {userId} = ? ) ";
			queryParams.add(params.get("userId"));
		}

		if (params.get("appInfo") != null) {
			where += " and ( concat(',',{category4},',')  like ? ) ";
			queryParams.add("%," + params.get("appInfo") + ",%");
		}

        if (params.get("createUser") != null) {
            where += " and ( {createUser} = ? ) ";
            queryParams.add(params.get("createUser"));
        }

        if (params.get("id") != null) {
            where += " and ( {id} = ? ) ";
            queryParams.add(params.get("id"));
        }

		if (state == -1) {
			where += " and ( {status} in (0,1) ) ";
		} else {
			where += " and ( {status} =? ) ";
			queryParams.add(state);
		}

		if (where.length() > 0) {
			where = where.trim().substring(3);
			sql += " where " + where;
		}

		sql += " order by {id} desc ";

		if (!count) {
			sql += " limit ?,? ";
			queryParams.add((pn - 1) * ps);
			queryParams.add(ps);
		}

		return count ? timetaskDao.queryValue(sql, queryParams).getInt(0) : timetaskDao.queryList(sql, queryParams);

	}

	public int getCount(String words, Map<String, Object> params, int state, int pn, int ps) throws Exception {
		return (Integer) this.getTimeTaskList(words, params, state, pn, ps, true);
	}

	public List<TimeTask> getList(String words, Map<String, Object> params, int state, int pn, int ps) throws Exception {
		return (List<TimeTask>) this.getTimeTaskList(words, params, state, pn, ps, false);
	}

	public static List<String> splitWords(String s) {
		if (s == null || s.trim().length() == 0)
			return null;

		List<String> result = new ArrayList<String>();
		int i = 0;
		int begin = 0; // "addd eeee 总共 cddddd"
		s = s + " ";
		while (i < s.length()) {
			char c = s.charAt(i);
			if (c <= 32) {
				if (i > begin)
					result.add(s.substring(begin, i));
				begin = i + 1;
			} else if (c >= 128) {
				if (i > begin)
					result.add(s.substring(begin, i));
				result.add(s.substring(i, i + 1));
				begin = i + 1;
			}
			i++;
		}
		return result;

	}

	private static String formatWords(String s) {
		if (s == null || s.trim().length() == 0)
			return s;

		StringBuilder sb = new StringBuilder(s.length());
		int i = 0;
		int begin = 0; // "addd eeee 总共 cddddd"
		s = s + " ";
		while (i < s.length()) {
			char c = s.charAt(i);
			if (c <= 32) {
				if (i > begin)
					sb.append(" AND \"" + s.substring(begin, i) + "\"");
				begin = i + 1;
			} else if (c >= 128) {
				if (i > begin)
					sb.append(" AND \"" + s.substring(begin, i) + "\"");
				sb.append(" AND \"" + s.substring(i, i + 1) + "\"");
				begin = i + 1;
			}
			i++;
		}
		String res = sb.toString().trim();
		if (res.startsWith("AND"))
			res = res.substring(3).trim();
		return res;

	}

	public static void main(String[] args) {
		System.out.println(formatWords("  1addd eeee   我10.10. 我  总共 cddddd"));
	}

	public void destroy() throws Exception {
	}

	public RemoteLogService getRemoteLogService(String host, String port) {
		RmiProxyFactoryBean factory = null;
		factory = new RmiProxyFactoryBean();
		factory.setServiceInterface(RemoteLogService.class);
		factory.setServiceUrl("rmi://" + host + ":" + port + "/remoteLogService");
		factory.afterPropertiesSet();
		return (RemoteLogService) factory.getObject();
	}

	public	ClientServiceProxy getClientServiceProxy(TimeTask timeTask){
		ClientServiceProxy clientServiceProxy=new ClientServiceProxy();
		String url=machineListService.getServiceUrl(timeTask.getAppName(),timeTask.getTargetIp());
		clientServiceProxy.setBaseUrl(url);
		return clientServiceProxy;

	}

	public Map<String, Set<String>> getPropsMap(int taskType,long userId) {
        String key=taskType+"_"+userId;
		Map<String, Set<String>> propsMap = propsMaps.get(key);
		if (propsMap == null || System.currentTimeMillis() - this.catLastModified > 1000 * 10) {
			synchronized (this) {
				propsMap = propsMaps.get(key);
				if (propsMap == null || System.currentTimeMillis() - this.catLastModified > 1000 * 10) {

					Date date = this.getLastModified();
					long time = date == null ? 0 : date.getTime();
					boolean changed = propsMap == null || dbLastModified != time;
					if (changed) {
						List<TimeTask> configs = timetaskDao.listValid(taskType,userId);
						Map<String, Set<String>> map = new HashMap<String, Set<String>>();

						//Set<String> targetIpSet = new TreeSet<String>(machineListService.getMachines(null));
						//map.put("targetIp",targetIpSet);



						Set<String>[] catSets = new TreeSet[CAT_SIZE + 1];
						for (int i = 1; i < CAT_SIZE + 1; i++) {
							catSets[i] = new TreeSet<String>();
							map.put("cat" + i, catSets[i]);
						}

						Set<String> beanNameSet = new TreeSet<String>();
						map.put("beanName", beanNameSet);
						for (TimeTask timeTask : configs) {


							String[] categories = timeTask.getCategories();
							for (int i = 0; i < categories.length; i++) {
								if (!StringUtils.isBlank(categories[i])) {
									catSets[i + 1].add(categories[i]);
								}
							}

							JSONObject json = TimeTaskUtil.getJSON(timeTask.getConfig());
							String beanName = json.getString("bean");
							if (!StringUtils.isBlank(beanName)) {
								beanNameSet.add(beanName);
							}

						}
						catLastModified = System.currentTimeMillis();
						dbLastModified = time;
						propsMap = map;
						propsMaps.put(key, propsMap);
					}
				}

			}

		}
		return propsMap;
	}

    /**
     * 合并并返还新的map
     * @param maps
     * @return
     */
    public Map<String, Set<String>> combinMapSet(Map<String, Set<String>>... maps){
        Map<String, Set<String>> map1=maps[0];
        Map<String, Set<String>> propsMap=new HashMap<String, Set<String>>(map1.size());
        for(String key:map1.keySet()){
            propsMap.put(key,new HashSet<String>(map1.get(key)));
        }
        for(int i=1;i<maps.length;i++) {
            Map<String, Set<String>> map2=maps[i];
            for (String key : map2.keySet()) {
                Set<String> set = propsMap.get(key);
                if (set == null) propsMap.put(key, new HashSet<String>(map2.get(key)));
                else set.addAll(map2.get(key));
            }
        }
        return propsMap;
    }

    /**
     * 分类排序
     * @param propsMap
     * @return
     */
    public Map<String, Set<String>> sortCats( Map<String, Set<String>> propsMap) {

        propsMap.put("cat4"  , fixChannels(propsMap.get("cat4")));
        for (int i = 0; i < TimeTaskService.CAT_SIZE + 1; i++) {
            propsMap.put("cat"+i  ,sortSet(propsMap.get("cat"+i)));
        }
        return propsMap;
    }


    private Set<String> sortSet(Set<String> set){
        String[] ss= set.toArray(new String[0]);
        Comparator com= Collator.getInstance(Locale.CHINA);
        Arrays.sort(ss,com);
        Set<String> set2=new LinkedHashSet<String>(Arrays.asList(ss));
        return set2;

    }
    private Set<String>   fixChannels(Set<String> set){

        Set<String> set2=new LinkedHashSet<String>(set.size());
        for(String s:set){
            String[] ss=s.split("[,;]+");
            for(String a:ss) if(a.length()>0) set2.add(a);
        }
        return set2;
    }



	public Date getLastModified() {
		return timetaskDao.getLastModified();
	}


}
