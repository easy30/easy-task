package com.cehome.task.dao;


import com.cehome.task.domain.TimeTask;
import com.cehome.task.domain.TimeTaskCache;
import jsharp.sql.SimpleDao;

import java.util.Date;
import java.util.List;

//@Component
public class TimeTaskCacheDao extends SimpleDao<TimeTaskCache> {

    private String tableName;

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
