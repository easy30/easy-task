package com.cehome.task.client.log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
public class ClientSocket{
        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;
        private String id;
        public ClientSocket(Socket socket) throws IOException {
            this.socket=socket;
            dis = new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());

            //id=read();

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void write(String s) throws IOException {
             write(dos,s);
        }

        public String read() throws IOException {
            return  read(dis);
        }

        public void close(){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static  void write(DataOutputStream dos, String s) throws IOException {

        byte[] bs=s.getBytes("UTF-8");
        dos.writeInt(bs.length);
        dos.write(bs);

    }
    private static String read(DataInputStream dis) throws IOException {
        int l=dis.readInt();
        byte[] bs=new byte[l];
        dis.readFully(bs);
        return new String(bs,"UTF-8");

    }

    }