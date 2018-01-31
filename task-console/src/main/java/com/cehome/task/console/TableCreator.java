package com.cehome.task.console;

import com.cehome.task.util.IpAddressUtil;
import jsharp.util.Common;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


@Component
public class TableCreator implements InitializingBean,BeanPostProcessor {


    private static final Logger logger = LoggerFactory.getLogger(TableCreator.class);
    @Value("${task.autoCreateTable:false}")
    private boolean autoCreateTable;

    @Value("${task.factory.appName}")
    private String appName;

    @Value("${task.factory.name}")
    private String table1;

    @Value("${task.fuseHostName:false}")
    private boolean useHostName;

    @Value("${task.datasource.url}")
    private String url;

    @Value("${task.datasource.username}")
    private String username;

    @Value("${task.datasource.password}")
    private String password;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(autoCreateTable) {
            execute();
        }
    }


    public  void execute() {

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;


        try
        {

            String table2=table1+"_cache";

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);


            try {
                logger.info("create begin");
                st = conn.createStatement();
                {
                    String sql = "show tables like '" + table1 + "'";

                    rs = st.executeQuery(sql);
                    if (rs.next()) {
                        logger.info("table " + table1 + " exists.");

                    }else{
                        File file = ResourceUtils.getFile("classpath:sql/task.txt");
                        String sql1 = FileUtils.readFileToString(file,"UTF-8");
                        sql1 = sql1.replace("${tableName}", table1);

                        sql1 = sql1.replace("${ip}", useHostName?IpAddressUtil.getLocalHostName(): IpAddressUtil.getLocalHostAddress());
                        sql1 = sql1.replace("${appName}", appName);
                        st.execute(sql1);
                        logger.info("created  table " + table1);
                    }
                    rs.close();

                }

                String sql="show tables like '"+table2+"'";
                rs= st.executeQuery(sql);
                if(rs.next()){
                    logger.info("table "+table2+" exists.");

                }else {
                    File file= ResourceUtils.getFile("classpath:sql/task_cache.txt");
                    String sql2=FileUtils.readFileToString(file,"UTF-8");
                    sql2=sql2.replace("${tableName}",table2);
                    st.execute(sql2);
                    logger.info("created  table " + table2);
                }

                logger.info("crate end");
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



    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
