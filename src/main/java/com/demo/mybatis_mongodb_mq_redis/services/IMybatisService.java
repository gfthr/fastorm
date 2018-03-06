package com.demo.mybatis_mongodb_mq_redis.services;

import com.demo.mybatis_mongodb_mq_redis.models.Mybatis;

import java.util.List;

public interface IMybatisService {

    List<Mybatis> list();
}
