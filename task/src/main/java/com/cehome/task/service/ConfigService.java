package com.cehome.task.service;

import java.util.Map;
import java.util.Set;

public interface ConfigService {


    boolean hset(String key, String field, String value);

    boolean hdel(String key, String... fields);

    boolean hexists(String key, String field);

    boolean expire(String key, int seconds);

    Map<String, String> hgetAll(String key);

    String hget(String key, String field);

    boolean simpleLock(String key, int timeout);

    boolean sadd(String key, String member);

    boolean srem(String key, String... members);

    Set<String> smembers(String key);

    boolean simpleUnlock(String key);

    long getTime();
}
