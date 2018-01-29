package com.cehome.task.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class HttpUtil {

	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	private static AtomicLong downloadStat = new AtomicLong(0);

	public static String getFullUrl(String curl, String file) {
		if (StringUtils.isBlank(curl))
			return file;
		if(file!=null && file.startsWith("data:")) return file;
		URL url = null;
		String q = "";
		try {
			url = new URL(new URL(curl), file);
			q = url.toExternalForm();
		} catch (MalformedURLException e) {

		}
		url = null;
		if (q.indexOf("#") != -1)
			q = q.replaceAll("^(.+?)#.*?$", "$1");
		return q;
	}



	public static String httpGet(String urlWithParams, String encoding,int msTimeout) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		// HttpGet httpget = new HttpGet("http://www.baidu.com/");
		HttpGet httpget = new HttpGet(urlWithParams);

		try {
			// 配置请求的超时设置
			if(msTimeout>0){
				RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(msTimeout).setConnectTimeout(msTimeout)
						.setSocketTimeout(msTimeout)
						.build();
				httpget.setConfig(requestConfig);
			}

			CloseableHttpResponse response = httpclient.execute(httpget);
			System.out.println("StatusCode -> " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
				//throw new Exception("Error " + response.getStatusLine().getStatusCode());
				throw new HttpResponseException(response.getStatusLine().getStatusCode(),"Error " + response.getStatusLine().getStatusCode()+" "+urlWithParams);
			}

			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, encoding);
		} finally {
			httpget.releaseConnection();
		}

	}

	private static List<NameValuePair> map2Pairs(Map<String,String> map){
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> e:map.entrySet()){
			params.add(new BasicNameValuePair(e.getKey(),e.getValue()) );
		}
		return params;
	}

	public static String httpPost(String url, Map<String,String>  params, String encoding) throws Exception {
		return httpPost(url,params,encoding,null);
	}
	public static String httpPost(String url, Map<String,String>  params, String encoding, Map<String,String> headers) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpPost httppost = new HttpPost(url);
		try {
			if(params!=null)
				httppost.setEntity(new UrlEncodedFormEntity(map2Pairs(params), encoding));
			if(headers!=null){
				for(Map.Entry<String,String> e: headers.entrySet()){
					httppost.setHeader(e.getKey(),e.getValue());
				}
				if(headers.get("User-Agent")==null)httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			}

			CloseableHttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new HttpResponseException(response.getStatusLine().getStatusCode(),"Error " + response.getStatusLine().getStatusCode()+" "+url);
			}

			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, encoding);
			return content;
		} finally {
			httppost.releaseConnection();
		}

	}

	private static String formatURL(String fileURL) throws URISyntaxException, MalformedURLException{
		try {
			return new URI(fileURL).toASCIIString();
		} catch (URISyntaxException e) {
			URL url = new URL(fileURL);
			return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null).toASCIIString();
		}
	}



	public static String reverseDomain(String domain) {
		String revDomain = "";
		if (StringUtils.isNotBlank(domain)) {
			String[] items = domain.split("\\.");
			if(ArrayUtils.isNotEmpty(items) && items.length == 1) {
				return domain;
			}
			for (int i = items.length - 1; i >= 0; i--) {
				revDomain += items[i];
				revDomain += ".";
			}
			if (StringUtils.isNotBlank(revDomain)) {
				revDomain = revDomain.substring(0, revDomain.length() - 1);
			}
		}

		return revDomain;
	}

	public  static boolean isDataUrl(String url){
		return url!=null && url.startsWith("data:");
	}



}
