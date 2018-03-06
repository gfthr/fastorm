package com.demo.mybatis_mongodb_mq_redis.daos;

import com.demo.mybatis_mongodb_mq_redis.models.Mybatis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MybatisMapper {

    List<Mybatis> getList();

}
