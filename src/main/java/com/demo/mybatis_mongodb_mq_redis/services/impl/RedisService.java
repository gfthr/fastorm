package com.demo.mybatis_mongodb_mq_redis.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

public class RedisService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> valOpsStr;


    public void saveStr(String key, String val) {
        valOpsStr.set(key, val);
    }

    public String getStr(String key) {
        return valOpsStr.get("key");
    }

}
