package com.cehome.task.client.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class LogServer extends Thread{
    protected static final Logger logger = LoggerFactory.getLogger(LogServer.class);
    static ServerSocket server = null;
    private Map<String,ClientSocket> clientMap= new ConcurrentReferenceHashMap();
    public LogServer() throws IOException {
        server = new ServerSocket(811);
        start();
    }





    @Override
    public void run(){
        try {
            ClientSocket client = new ClientSocket(server.accept());
            client.setId(client.read());
            ClientSocket oldClient=  clientMap.get(client.getId());

            if(oldClient!=null){
                oldClient.close();
            }
            clientMap.put(client.getId(),client);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {

    }
}
