## Easy Task 概述
简单易用的分布式任务调度平台。来源于淘宝彩票调度平台，并先后在淘宝内容抓取平台和铁甲二手机任务调度平台中使用，目前重构并开源。具有如下特点：
- 去中心化。
- 支持上百台不同业务的应用服务器接入。任务在每个应用上独立执行，充分利用应用本身资源。
- 故障转移。应用如有多台执行机器，一台机器挂掉，其上的任务会切到其它机器。
- 管理控制台统一对任务修改、启动、停止等。控制台挂掉不影响任务的执行。
- 可以在线查看任务日志，实时了解任务执行情况。


![架构图](https://raw.githubusercontent.com/cehome-com/easy-task/master/docs/images/system.png)

## 快速体验
下载并启动一个控制台。控制台同时也是worker，也能执行任务，缺省会启动一个内置的demoPlugin任务。

-  下载并启动

1）方式一：到release中下载或直接下载可执行jar包 https://github.com/cehome-com/resource/raw/master/easy-task/2.0.2/task-console.jar

  然后执行命令启动： java -jar task-console.jar
  
2） 方式二：直接剪出task-console spring boot代码模块，导入IDE中，执行com.cehome.task.console.TaskConsoleApplication启动。

如果你想快速部署一套简单可用的调度系统，可以采用方式二，在task-console代码里面添加插件，并部署使用。

如果你有多个应用，想接入调度平台，采用方式一，部署的console只做管理，不做任务执行。

- 访问 http://localhost:8080 ，没有意外的话应该看到一个demo任务

![demo图](https://raw.githubusercontent.com/cehome-com/easy-task/master/docs/images/main.png)

- 点击“查看日志”按钮，可以看到任务执行日志（如果没有，可以停10秒再刷新一下）
- 点击“修改”查看或修改任务配置。系统基于spring，Bean名称“demoPlugin"就是内置的一个spring bean。
- 点击“停止”可以停止任务。


注：

1）控制台缺省内置了一个H2数据库（端口9092）来保存任务配置。你也可以采用外部H2或mysql数据库。

2）采用方式二剪出task-console代码模块，例子中的demoPlugin对应类为com.cehome.task.console.DemoPlugin，你可以直接修改此插件。

## 模拟客户端应用（worker）接入调度平台
实际使用中，console只是管理任务，不执行任务，任务是在客户端应用中执行的。下面模拟app1和app2两个应用接入调度平台。为了方便，还是用task-console.jar来模拟。执行前，先保证上面的console还在运行状态。

- 启动另一个命令行窗口，执行如下命令启动app1（端口为8091）

**java -jar task-console.jar --task.factory.appName=app1 --server.port=8091**


-启动另一个命令行窗口，执行如下命令启动app2， （端口为8092）

**java -jar task-console.jar --task.factory.appName=app2 --server.port=8092**

- 访问http://localhost:8080 ，点击列表中demo的“修改”按钮，弹出修改界面，点击“应用”下拉框，应该能看到app1、app2也在里面，选择app2，然后保存并关闭。
- 观察app2的命令行输出窗口，发现demo已经转移到在app2中执行了。

## 现有spring boot应用接入调度平台
以 task-spring-boot-client-demo 模块来说明spring boot应用如何接入调度平台。
- 增加依赖

```
        <dependency>
            <groupId>com.cehome</groupId>
            <artifactId>task</artifactId>
            <version>2.0.2</version>
        </dependency>
```

- 加入@EnableTimeTaskClient注解


```
package com.cehome.task.client.demo;

import com.cehome.task.annotation.EnableTimeTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTimeTaskClient
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class,args);
    }

}

```
 

- 修改配置信息application.properties

需求配置的信息说明：

task.factory.appName - 应用名称，不同应用应该不一样。

task.factory.name - 集群名称，同时也是数据库表名，定了以后不要随意改动。
task.datasource.* 配置数据库信息，支持H2和mysql数据库，建议生产环境使用mysql数据库。

task.log.path - logback任务日志输出路径。
task.log.packages - 任务执行类所在包名，记录日志用。多个包名用半角分号隔开。如果不好确定，就用ROOT根日志。
```
spring.application.name=boot-client-demo
server.port=8081

#------  main options --------
#应用的名称
task.factory.appName=boot-client-demo
#集群名称(同时也是数据库表名)
task.factory.name=easy_task

#h2数据库配置
task.datasource.driverClassName=org.h2.Driver
task.datasource.url=jdbc:h2:tcp://localhost:9092/~/easy_task_db;MODE=MYSQL
task.datasource.username=sa
task.datasource.password=

#------  client options --------
task.log.packages=ROOT
task.log.path=/logs/easy_task/boot_demo
```

- 开发任务插件

任务插件可以继承com.cehome.task.client.TimeTaskPlugin，由于执行方法是run()是固定的，在console配置任务信息时候就可以不指定方法名。stop()方法会在点击停止任务时候触发，代码应用停止任务执行和释放必要的资源。


```
package com.cehome.task.client.demo;

import com.alibaba.fastjson.JSONObject;
import com.cehome.task.client.TimeTaskContext;
import com.cehome.task.client.TimeTaskPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;

@Component
public class BootDemoPlugin extends TimeTaskPlugin {
    private static final Logger logger = LoggerFactory.getLogger(BootDemoPlugin.class);
    @Override
    public void run(TimeTaskContext context, JSONObject args) throws Exception {
        logger.info("plugin class name="+this);
        logger.info("task id="+context.getId()+",name="+context.getName());
        logger.info("task run on ip="+ Inet4Address.getLocalHost().getHostAddress());
        logger.info("task run count="+context.getRunTimes());
    }

    @Override
    public void stop(TimeTaskContext context) throws Exception {
        logger.info("task "+context.getName()+" is stopped ");
    }
}



```


任务插件也可以是普通的spring bean，但在console配置任务信息时候，需要指定要执行的方法。

- 启动应用task-spring-boot-client-demo
- 访问http://localhost:8080 （确保控制台是启动状态的），点击“添加”任务，
 “应用”选择“boot-client-demo”；计划时间填5s（5秒）；Bean名称跟上面开发插件一致，填写“bootDemoPlugin”；其它必填字段自己随意。
![添加任务](https://github.com/cehome-com/easy-task/raw/master/docs/images/addTask.png)
- 保存并关闭，点击“启动”，然后过10多秒钟点击“查看”日志，如果看到“task run……”日志，说明一切正常。
-


## 现有spring mvc应用接入调度平台
以 task-spring-mvc-client-demo 模块来说明。
- 增加依赖

```
        <dependency>
            <groupId>com.cehome</groupId>
            <artifactId>task</artifactId>
            <version>2.0.2</version>
        </dependency>
```

- spring xml中导入bean：

```
<import resource="classpath*:task-client-spring-config.xml"></import>

```

- 在spring xml 中引入配置信息spring/config.properties


```
  <context:annotation-config/>
<context:property-placeholder location="classpath*:spring/config.properties"/>

```


- 在对应的spring/config.properties添加配置信息：

task.factory.appName - 应用名称，不同应用应该不一样。

task.factory.name - 集群名称，同时也是数据库表名，定了以后不要随意改动。
task.datasource.* 配置数据库信息，支持H2和mysql数据库，建议生产环境使用mysql数据库。

task.log.path - logback任务日志输出路径。
task.log.packages - 任务执行类所在包名，记录日志用。多个包名用半角分号隔开。如果不好确定，就用ROOT根日志。
```
task.factory.appName=mvc-client-demo
task.factory.name=easy_task

#h2
task.datasource.driverClassName=org.h2.Driver
task.datasource.url=jdbc:h2:tcp://localhost:9092/~/easy_task_db;MODE=MYSQL
task.datasource.username=sa
task.datasource.password=

#------  client options --------
task.log.packages=com.cehome.task.client.demo
task.log.path=/logs/easy_task/mvc_demo


```
- 在spring mvc xml配置远程在线日志查看的controller。 不配这个controller则console无法在线连接到应用查看日志。

```

	<context:component-scan
			base-package="com.cehome.task.client.controller"/>
	<mvc:annotation-driven />
```


- 开发任务插件

任务插件可以继承com.cehome.task.client.TimeTaskPlugin，由于执行方法是run()是固定的，在console配置任务信息时候就可以不配置。stop()方法会在点击停止任务时候触发，代码应用停止任务执行和释放必要的资源。


```
package com.cehome.task.client.demo;

import com.alibaba.fastjson.JSONObject;
import com.cehome.task.client.TimeTaskContext;
import com.cehome.task.client.TimeTaskPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;

@Component
public class MvcDemoPlugin extends TimeTaskPlugin {
    private static final Logger logger = LoggerFactory.getLogger(MvcDemoPlugin.class);
    @Override
    public void run(TimeTaskContext context, JSONObject args) throws Exception {
        logger.info("plugin class name="+this);
        logger.info("task id="+context.getId()+"task name="+context.getName());
        logger.info("task run on ip="+ Inet4Address.getLocalHost().getHostAddress());
        logger.info("task run count="+context.getRunTimes());
    }

   @Override
    public void stop(TimeTaskContext context) throws Exception {
        logger.info("task "+context.getName()+" is stopped ");
    }
}



```


任务插件也可以是普通的spring bean，但在console配置任务信息时候，需要指定要执行的方法。

- 在spring xml 加入插件的扫描路径

```
   <context:component-scan  base-package="com.cehome.task.client.demo"/>

```


- 启动应用task-spring-mvc-client-demo
- 访问console（不是mvc-demo）http://localhost:8080 （确保控制台是启动状态的），点击“添加”任务，
 应用选择“mvc-client-demo”；计划时间填5s（5秒）；Bean名称跟上面开发插件一致，填写“mvcDemoPlugin”；其它必填字段自己随意。
- 保存并关闭，点击“启动”，然后过10多秒钟点击“查看”日志，如果看到“task run……”日志，说明一切正常。


## 使用外部数据库
缺省的情况下，console会启动一个内部的数据库，生产环境建议用外部数据库。还是以H2数据库来说明：

- 启动H2数据库

到 http://www.h2database.com/html/download.html 下载h2 数据库并解压，进入bin目录，执行命令启动数据库（9092是数据库访问端口）。

 java -cp h2*.jar org.h2.tools.Server -tcpPort 9092  -tcpAllowOthers  -webPort 8082 -webAllowOthers 

- 修改console或client数据库配置信息
  
```

task.datasource.driverClassName=org.h2.Driver
task.datasource.url=jdbc:h2:tcp://192.168.0.10:9092/~/easy_task_db;MODE=MYSQL
task.datasource.username=sa
task.datasource.password=
```
  如果是console，可以修改task.h2.start=false 表示禁用内部数据库
  
```
task.h2.start=false

```

## mysql数据库配置参考

```
task.datasource.driverClassName=com.mysql.jdbc.Driver
task.datasource.url=jdbc:mysql://192.168.0.13:3306/scheduler?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true
task.datasource.username=root
task.datasource.password=123456
```

## 最佳实践建议
- 独立mysql数据库
- console 至少两个node
- 若干应用，每个应用至少保持两个node


## 配置信息详细说明


## 控制台操作说明







