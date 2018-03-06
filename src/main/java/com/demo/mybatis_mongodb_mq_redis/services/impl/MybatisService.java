package com.demo.mybatis_mongodb_mq_redis.services.impl;

import com.demo.mybatis_mongodb_mq_redis.daos.MybatisMapper;
import com.demo.mybatis_mongodb_mq_redis.models.Mybatis;
import com.demo.mybatis_mongodb_mq_redis.services.IMybatisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MybatisService implements IMybatisService {

    @Resource
    private MybatisMapper mybatisMapper;

    @Override
    public List<Mybatis> list() {
        return mybatisMapper.getList();
    }

}
