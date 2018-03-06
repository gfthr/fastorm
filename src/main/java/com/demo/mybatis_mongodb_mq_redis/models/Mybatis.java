package com.demo.mybatis_mongodb_mq_redis.models;

import lombok.Getter;
import lombok.Setter;

@Setter  //生成set方法
@Getter  //生成get方法
public class Mybatis {

    private long id;
    private String name;
    private int sex;


}
