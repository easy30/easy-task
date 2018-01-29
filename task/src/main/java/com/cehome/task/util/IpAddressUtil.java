package com.cehome.task.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class IpAddressUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressUtil.class);

	/**
	 * 获取IP地址列表
	 * @return IP地址列表
	 */
	public static List<InetAddress> getIpAddressList(){
		List<InetAddress> iplist = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while(netInterfaces.hasMoreElements())
			{
				NetworkInterface ni = netInterfaces.nextElement();
				if(!ni.isUp()) continue;;
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while(ips.hasMoreElements()){
					InetAddress ip= ips.nextElement();
					if(ip instanceof Inet4Address){
						//IPv4才处理
						if(!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
							if(!ip.equals("127.0.0.1")){
								iplist.add(ip);
							}

						}
					}else{
						//
					}

				}
			}
		}catch(SocketException e) {
		    LOGGER.error(e.getMessage(), e);
		}
		return iplist;
	}

    public static Set<String> getIpAddressSet(){
        Set<String> ipSet = new LinkedHashSet<String>();
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while(netInterfaces.hasMoreElements())
            {
                NetworkInterface ni = netInterfaces.nextElement();
                if(!ni.isUp()) continue;
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while(ips.hasMoreElements()){
                    InetAddress ip= ips.nextElement();
                    if(ip instanceof Inet4Address){
                        //IPv4才处理
                        if(!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
                            if(!ip.equals("127.0.0.1")){
                                ipSet.add(ip.getHostAddress());
                            }

                        }
                    }else{
                        //
                    }

                }
            }
        }catch(SocketException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ipSet;
    }

	public static String getLocalHostName(){
		try {
			return (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException uhe) {
			//java.net.UnknownHostException: Centos-37: Centos-37: Name or service not known
			String host = uhe.getMessage();
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					return host.substring(0, colon).trim();
				}
			}
			return null;
		}
	}

	public static String getLocalHostAddress()  {

		try {
			InetAddress address = InetAddress.getLocalHost();
			String localIP = address.getHostAddress();
			return localIP;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;

	}


	public static Set<String> getHostNameSet(){
		Set<String> ipSet = new LinkedHashSet<String>();
		Enumeration<NetworkInterface> netInterfaces;
		ipSet.add(getLocalHostName());
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while(netInterfaces.hasMoreElements())
			{
				NetworkInterface ni = netInterfaces.nextElement();
				if(!ni.isUp()) continue;
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while(ips.hasMoreElements()){
					InetAddress ip= ips.nextElement();
					if(ip instanceof Inet4Address){
						//IPv4才处理
						if(!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
							if(!"localhost".equals(ip.getHostName())){
								ipSet.add(ip.getHostName());
							}

						}
					}else{
						//
					}

				}
			}
		}catch(SocketException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return ipSet;
	}

	public static boolean matchIP(String targetIp){
		List<InetAddress> ipAddr = IpAddressUtil.getIpAddressList();
		for(InetAddress ip : ipAddr){
			if(ip.getHostAddress().equals(targetIp)){
				return true;
			}
		}
		return false;
	}

	public static boolean matchIpPattern(String str){
		if(str == null) return false;
		Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	public static void main(String[] args) throws UnknownHostException {
		List<InetAddress> ipAddr = IpAddressUtil.getIpAddressList();
		try {
			InetAddress address = InetAddress.getLocalHost();
			System.out.println(address.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ipAddr.size());
		System.out.println(ipAddr.get(0).getHostAddress());
	}
}
