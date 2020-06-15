package com.cehome.task.client.log;

import com.alibaba.fastjson.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LogProducer {
    Socket socket=null;
    ClientSocket clientSocket=null;
    public LogProducer(String host,int port) throws IOException {
        socket=new Socket(host,port);
        String id=host+":"+port;
        clientSocket=new ClientSocket(socket);
        clientSocket.setId(id);
        clientSocket.write(id);
    }

    public String getLog(String logName, long pageNo, long pageSize) throws IOException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("method","getLog");
        JSONObject  args=new JSONObject();
        args.put("logName",logName);
        args.put("pageNo",pageNo);
        args.put("pageSize",pageSize);
        jsonObject.put("args",args);
        clientSocket.write(jsonObject.toJSONString());
        clientSocket.read();

        return null;


    }



}
