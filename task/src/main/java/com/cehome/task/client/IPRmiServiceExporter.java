package com.cehome.task.client;

import java.net.UnknownHostException;

import org.springframework.remoting.rmi.RmiServiceExporter;

public class IPRmiServiceExporter extends RmiServiceExporter{

	public IPRmiServiceExporter(){

		try {
			System.setProperty("java.rmi.server.hostname", java.net.InetAddress.getLocalHost().getHostAddress());
			//this.setRegistryHost(java.net.InetAddress.getLocalMachine().getHostAddress());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}
