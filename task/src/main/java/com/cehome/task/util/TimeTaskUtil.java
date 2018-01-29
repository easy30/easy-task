package com.cehome.task.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class TimeTaskUtil {
	public static JSONObject getJSON(String s) {
		if (s != null && s.trim().length() > 0) {
			try {
				return JSONObject.parseObject(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new JSONObject();
	}

	public static <T> T getJSON(String s,Class<T> clazz) {
		if (s != null && s.trim().length() > 0) {
			try {
				return JSONObject.parseObject(s,clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			return  clazz.newInstance();
		} catch ( Exception e) {
			 throw  new RuntimeException(e);
		}
	}

	
	public static String getFullUrl(HttpServletRequest request) throws IOException {

		String url = request.getServletPath();
		if (!request.getContextPath().equals("/"))
			url = request.getContextPath() + url;
		if (request.getQueryString() != null) {
			url += "?" + request.getQueryString();
		}
		return url;

	}
	public static String removeParam(String query, String name) {
		int n=query.indexOf('?');
		if(n==-1) return query+"?";
		n = query.indexOf(name + "=",n+1);
		if (n >= 0) {
			int begin = n>0&&query.charAt(n-1)=='&'?n-1: n;
			int end = query.indexOf('&', begin+1);
			if (end == -1)
				return query.substring(0, begin);
			else
				return query.substring(0, begin) + query.substring(end);
		}
		return query;
	}

	public static Object  registerBean(AbstractAutowireCapableBeanFactory factory, String name, Object bean){

		factory.autowireBean(bean);
		factory.registerSingleton(name,bean);
		factory.initializeBean(bean,name);
		return bean;
	}
	
}