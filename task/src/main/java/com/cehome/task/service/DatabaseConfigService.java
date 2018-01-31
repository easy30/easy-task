package com.cehome.task.service;

import com.cehome.task.dao.TimeTaskCacheDao;
import com.cehome.task.domain.TimeTaskCache;
import jsharp.sql.SessionFactory;
import jsharp.util.DataValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DatabaseConfigService implements ConfigService,InitializingBean {

    @Autowired
    TimeTaskCacheDao timeTaskCacheDao;
    SessionFactory sessionFactory;
    private static String EXPIRE="( expire is null or expire>now())";
    @Override
    public boolean hset(String key, String field, String value) {
        String sql="replace into "+timeTaskCacheDao.getTableName()+" (main_key, sub_key,value,create_time,update_time,expire) values(?,?,?,now(),now(),null)";
        sessionFactory.updateBySQL(sql,key,field,value);
        return true;
    }

    @Override
    public boolean hdel(String key, String... fields) {
        StringBuilder sb=new StringBuilder();
        for(String f:fields){
            if(sb.length()>0){
                sb.append(',');
            }
            sb.append("'"+f+"'");

        }

        String sql="delete from "+timeTaskCacheDao.getTableName()+" where main_key=? and sub_key in ("+sb+")";
        return  sessionFactory.updateBySQL(sql,key)>0;

    }

    @Override
    public boolean hexists(String key, String field) {
        String sql="select count(*) from "+timeTaskCacheDao.getTableName()+" where main_key=? and sub_key=? and "+EXPIRE;
        DataValue dataValue= timeTaskCacheDao.queryValue(sql,key,field);
        return dataValue!=null && dataValue.getInt(0)>0;
    }

    @Override
    public boolean expire(String key, int seconds) {
        String sql="update "+timeTaskCacheDao.getTableName()+" set expire=date_add(expire, interval ? second)  where  main_key=? and "+EXPIRE;
        return sessionFactory.updateBySQL(sql,seconds,key)>1;

    }

    @Override
    public Map<String, String> hgetAll(String key) {
        List<TimeTaskCache> list=timeTaskCacheDao.queryList("main_key=? and "+EXPIRE,key);
        Map<String,String> result=new HashMap<>();
        for(TimeTaskCache timeTaskCache:list){
            result.put(timeTaskCache.getSubKey(),timeTaskCache.getValue());
        }
        return result;


    }

    @Override
    public String hget(String key, String field) {
        String sql="select value from "+timeTaskCacheDao.getTableName()+" where main_key=? and sub_key=? and "+EXPIRE;
        DataValue dataValue= timeTaskCacheDao.queryValue(sql,key,field);
        if(dataValue==null) return null;
        return  dataValue.getStr();

    }



    @Override
    public boolean sadd(String key, String member) {
        String sql="replace into "+timeTaskCacheDao.getTableName()+" (main_key, sub_key,value,create_time,update_time,expire) values(?,'0',?,now(),now(),null)";
        sessionFactory.updateBySQL(sql,key,member);
        return true;
    }

    @Override
    public boolean srem(String key, String... members) {
        StringBuilder sb=new StringBuilder();
        for(String f:members){
            if(sb.length()>0){
                sb.append(',');
            }
            sb.append(f);

        }

        String sql="delete from "+timeTaskCacheDao.getTableName()+" where main_key=?  value in ("+sb+")";
        return  sessionFactory.updateBySQL(sql,key)>0;
    }

    @Override
    public Set<String> smembers(String key) {
        List<TimeTaskCache> list=timeTaskCacheDao.queryList("main_key=? and "+EXPIRE,key);
        Set<String> set=new HashSet<>();
        for(TimeTaskCache timeTaskCache:list){
            set.add(timeTaskCache.getValue());
        }
        return set;
    }

    @Override
    public boolean simpleLock(String key, int timeout) {
        if(timeout<=0) throw new RuntimeException("timeout must >0");
        TimeTaskCache timeTaskCache= timeTaskCacheDao.queryOne("select now() update_time, expire,version from "+ timeTaskCacheDao.getTableName()
                +" where main_key=?",key);
        if(timeTaskCache==null){
            String sql="insert into "+timeTaskCacheDao.getTableName()+"(main_key, sub_key,value,expire,create_time,update_time) values(?,?,?,date_add(now(), interval ? second),now(),now())";
            try {
                sessionFactory.updateBySQL(sql,key,"0","1",timeout);
                return true;
            }catch (Exception e){
                return false;
            }


        }else{
            if(timeTaskCache.getUpdateTime().before(timeTaskCache.getExpire())){
                return false;
            }
            long version=timeTaskCache.getVersion();
            String sql="update "+timeTaskCacheDao.getTableName()+" set version=version+1, expire=date_add(expire, interval ? second) " +
                    " where  main_key=? and version=? ";
            return sessionFactory.updateBySQL(sql,timeout,key,version)>0;
        }

    }

    @Override
    public boolean simpleUnlock(String key) {
        String sql="update "+timeTaskCacheDao.getTableName()+" set expire=date_add(now(), interval -1 second) where  main_key=? " ;
        return sessionFactory.updateBySQL(sql,key)>0;
        // timeTaskCacheDao.deleteByWhere("main_key=?",key);
       // return true;
    }

    @Override
    public long getTime() {
       Date date=(Date) sessionFactory.queryValue("select now()").getValue();
       return date.getTime();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sessionFactory=timeTaskCacheDao.getSessionFactory();
    }
}
