package com.cehome.task.service;

import org.springframework.beans.factory.DisposableBean;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Created by coolma on 2017/9/18.
 */
//@Service
public class RedisConfigService implements ConfigService,DisposableBean {
    //Redis服务器IP
    private  String ADDR ;// Constants.REDIS_ADDR;

    //Redis的端口号
    private  int PORT ;//= Constants.REDIS_PORT;

    //访问密码
   // private static String AUTH = "admin";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 3;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    //private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    //private static boolean TEST_ON_BORROW = true;

    private  JedisPool jedisPool = null;

    private  JedisCluster jedisCluster;
    private  boolean clusterMode=false;


    /**
     * 初始化Redis连接池
     */

    public RedisConfigService(String addr, int port)
      {
        try {
            ADDR=addr;
            PORT=port;
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);

            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RedisConfigService(String nodes){

        Set<HostAndPort> hostAndPortsSet = new HashSet<HostAndPort>();
        for(String node :nodes.split(",")){
            String[] hp=node.split(":");
            hostAndPortsSet.add(new HostAndPort(hp[0], Integer.parseInt(hp[1])));
        }


        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(MAX_ACTIVE);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisCluster = new JedisCluster(hostAndPortsSet, jedisPoolConfig);
        clusterMode=true;

    }

    @Override
    public void destroy() throws Exception {
        if(jedisCluster!=null){
            jedisCluster.close();
        }
        if(jedisPool!=null){
            jedisPool.close();
        }

    }

    /**
     * 获取Jedis实例
     * @return
     */
    private JedisCommands getJedis() {
        try {
            if (!clusterMode) {
                JedisCommands resource = jedisPool.getResource();
                return resource;
            } else {
                return jedisCluster;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void closeJedis(final JedisCommands jedis){
        if(clusterMode) return;
        if(jedis!=null)((Jedis)jedis).close();
    }
    private List<String> getTime(final JedisCommands jedis){
        if(clusterMode){
            String script = "local ntime = redis.call('TIME') return ntime";
            ArrayList<String> eval = (ArrayList<String>)jedisCluster.eval(script, "1");
            return eval;

        }else {
            return  ((Jedis) jedis).time();
        }
    }



    /**
     * 释放jedis资源

     */
 /*   public  void returnResource(final JedisCommands jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
*/
    @Override
    public   boolean hset(String key, String field, String value) {
        JedisCommands jedis=null;
        try {
                jedis = getJedis();
                jedis.hset(key,field,value);
                return true;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public   boolean hdel(final String key, final String... fields) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.hdel(key,fields)==1;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public  boolean hexists(final String key, final String field) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.hexists(key,field);
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public   boolean expire(String key, int seconds) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.expire(  key,   seconds)==1;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public String hget(String key, String field) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.hget(key,field);
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public boolean simpleLock(String key, int timeout) {
        JedisCommands jedis = null;
        try {
            jedis = getJedis();
            if (jedis.setnx(key, "" + (System.currentTimeMillis() + timeout * 1000)) == 1) {
                jedis.expire(key, timeout);
                return true;
            }


            String s = jedis.get(key);
            if (s == null) {
                return false; //no happend
            }

            long left = jedis.ttl(key);


            //当 key存在但没有设置剩余生存时间时，返回 -1(说明expire异常了），设置5秒过去
            if (left == -1) {
                jedis.setex(key, 5, "" + (System.currentTimeMillis() + timeout * 1000));
            }
            return false;

        } finally {
            closeJedis(jedis);
        }
    }


    @Override
    public   boolean sadd(String key, String member) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.sadd(key,member)==1;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public   boolean srem(String key, String... members) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.srem(key,members)==1;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public Set<String> smembers(String key) {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public boolean simpleUnlock(String key){
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
             jedis.del(key);
             return true;
        }finally {
            closeJedis(jedis);
        }
    }

    @Override
    public long getTime() {
        JedisCommands jedis=null;
        try {
            jedis = getJedis();
            List<String>  time=getTime(jedis);
            // second*1000  , us/1000
            long ms=Long.parseLong(time.get(0))*1000+  Long.parseLong(time.get(1))/1000;
            return ms;
            //return Long.parseLong(time.get(0)+time.get(1).substring(0,3));
        }finally {
            closeJedis(jedis);
        }

    }


    public static void main(String[] args) {
        //ConfigService configService=new ConfigService();
        //configService.
        RedisConfigService redisConfigService=new RedisConfigService("192.168.0.54",6379);
        List<String> time=((Jedis)redisConfigService.getJedis()).time();
        System.out.println(System.currentTimeMillis());
        System.out.println(redisConfigService.getTime());
        for(String s:time)
        System.out.println(s);

    }


}
