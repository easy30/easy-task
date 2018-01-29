package com.cehome.task.dao;


import com.cehome.task.domain.TimeTask;
import jsharp.sql.SimpleDao;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//@Component
public class TimeTaskDao  extends SimpleDao<TimeTask> {
    //public static String TABLE_NAME= Constants.TABLE;
    //@Resource
    //Sequence seqCrawlerTimeTask;
	/*@Override
	public int save(TimeTask entity, String... keys) {
		 if(entity.getId()==0 && (ArrayUtils.isEmpty(keys))){
			 try {

			 	long id =seqCrawlerTimeTask.nextValue();
				 entity.setId(id);
				 return this.insert(entity);
			 } catch (SequenceException e) {
				 throw new JSException(e);
			 }
		 }
		 else return super.save(entity,keys);
	}*/

	private String tableName;

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TimeTask> listByAppName(String appName){
        return this.queryListByProps(null, "appName",appName);
    }

    public List<TimeTask> list(String appName,int taskType){
        return this.queryListByProps(null, "appName",appName,"taskType",taskType);
    }

    public List<TimeTask> listValid( int taskType,long userId){
        return this.queryList(  " {taskType}=? and status<2 and {userId}=? " ,taskType,userId);

    }

    public List<TimeTask> listValid( ){
        List<TimeTask> list= this.queryList("select distinct({appName},{targetIp}) from "+getTableName()
                +" where  status<2 " );
       return list;

    }


    public List<TimeTask> listByIp(String appName,String targetIp){
        return this.queryListByProps(null, "appName",appName,"targetIp",targetIp);
    }

    public List<TimeTask> listByLastIp(String appName,String lastTargetIp){
        return this.queryListByProps(null, "appName",appName,"lastTargetIp",lastTargetIp);
    }
    public Date getLastModified() {
        return (Date) queryValue("select max(update_time) from " + getTableName()).getValue();
    }

}
