package com.cehome.task.client;

import com.cehome.task.Constants;
import com.cehome.task.TimeTaskClient;
import com.cehome.task.TimeTaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class ClientServiceController {

    protected static final Logger logger = LoggerFactory.getLogger(ClientServiceController.class);
    //private String contextPath;
    //private String port;
    //private String scheme;

    //private static String baseUrl;

    @Autowired
    protected TimeTaskFactory timeTaskFactory;
    @Autowired
    private TimeTaskClient timeTaskClient;
    //private String logPath;//= Constants.CLIENT_LOG_PATH;
    //private String logEncoding;
    //private boolean inited=false;
    //WebApplicationContext applicationContext;

    /*public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getLogEncoding() {
        return logEncoding;
    }

    public void setLogEncoding(String logEncoding) {
        this.logEncoding = logEncoding;
    }*/
   /* public static String getBaseUrl() {
        return baseUrl;
    }
*/

    @ResponseBody
    @RequestMapping(value=Constants.CLIENT_SERVICE_URL_STATUS, produces="text/html;charset=UTF-8")
    public String status(@RequestParam(value = "p", required = false, defaultValue = "0") String p) {
       return p;
    }

    @ResponseBody
    @RequestMapping(value=Constants.CLIENT_SERVICE_URL_GET_LOG, produces="text/html;charset=UTF-8")
    public String getLog(String logName, long pageNo, long pageSize) {
        String filename = timeTaskClient.getLogPath()+"/"+logName;
        LogShow logShow = new LogShow(filename,timeTaskClient.getLogEncoding());
        java.io.StringWriter w = new java.io.StringWriter((int) pageSize + 10);
        try {
            logShow.displayReverse(w, -1, pageNo, pageSize);
            return w.toString();
        } catch (IOException e) {
            logger.error("", e);
            return "";
        }

    }

    @ResponseBody
    @RequestMapping(value=Constants.CLIENT_SERVICE_URL_GET_LOG_SIZE, produces="text/html;charset=UTF-8")
    public String getLogSize(String logName) {
        String filename =timeTaskClient.getLogPath()+"/"+logName;
        LogShow logShow = new LogShow(filename, timeTaskClient.getLogEncoding());
        return ""+logShow.getTotalSize();
    }







}
