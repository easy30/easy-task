package com.cehome.task.domain;

import jsharp.sql.BaseDO;
import jsharp.sql.anno.Table;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Table(name="temp_time_task_cache")
public class TimeTaskCache extends BaseDO {
	@Id
	private long id; //主键
	private String mainKey;
	private String subKey;
	private String value;
	private int dataType;
	private Date expire;
	private long version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMainKey() {
		return mainKey;
	}

	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}

	public String getSubKey() {
		return subKey;
	}

	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
