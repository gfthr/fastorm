package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelObject;
import com.chineseall.orm.annotations.*;

/**
 * Created by wangqiang on 2018/2/25.
 */
@Database(name = "testdb1")
@Table(name = "auto", generate = GeneratorType.AUTO, engine = ModelEngineType.CACHE_MYSQL_OBJECT)
public class Auto extends ModelObject<Auto> {
    @Id
    @Column
    private Integer id;
    @Column
    private String name;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}