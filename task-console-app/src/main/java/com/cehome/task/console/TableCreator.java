package com.cehome.task.console;

import com.cehome.task.util.IpAddressUtil;
import jsharp.util.Common;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

public class TableCreator {
    public static void execute() {

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;


        ResourceBundle rb=ResourceBundle.getBundle("application", Locale.ROOT);

        try
        {
            String user=rb.getString("task.datasource.username");
            String url=rb.getString("task.datasource.url");
            String password=rb.getString("task.datasource.password");
            String appName=rb.getString("task.factory.appName");
            String table1=rb.getString("task.factory.name");
            String table2=table1+"_cache";

            boolean useHostName=false;
            try{
                useHostName="true".equalsIgnoreCase(rb.getString("task.useHostName"));
            }catch (Exception e){

            }

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);


            try {
                System.out.println("create begin");
                st = conn.createStatement();
                {
                    String sql = "show tables like '" + table1 + "'";

                    rs = st.executeQuery(sql);
                    if (rs.next()) {
                        System.out.println("table " + table1 + " exists.");

                    }else{
                        File file = ResourceUtils.getFile("classpath:sql/task.txt");
                        String sql1 = FileUtils.readFileToString(file);
                        sql1 = sql1.replace("${tableName}", table1);

                        sql1 = sql1.replace("${ip}", useHostName?IpAddressUtil.getLocalHostName(): IpAddressUtil.getLocalHostAddress());
                        sql1 = sql1.replace("${appName}", appName);
                        st.execute(sql1);
                        System.out.println("created  table " + table1);
                    }
                    rs.close();

                }

                String sql="show tables like '"+table2+"'";
                rs= st.executeQuery(sql);
                if(rs.next()){
                    System.out.println("table "+table2+" exists.");

                }else {
                    File file= ResourceUtils.getFile("classpath:sql/task_cache.txt");
                    String sql2=FileUtils.readFileToString(file);
                    sql2=sql2.replace("${tableName}",table2);
                    st.execute(sql2);
                    System.out.println("created  table " + table2);
                }

                System.out.println("crate end");
            }finally {
                Common.closeObjects(st);
            }



        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally
        {
            Common.closeObjects(rs,st,conn);

        }


    }

    public static void main(String[] args) throws Exception {
        execute();
    }
}
