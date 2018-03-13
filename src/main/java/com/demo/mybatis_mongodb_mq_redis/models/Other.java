package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelObject;
import com.chineseall.orm.annotations.*;

/**
 * Created by wangqiang on 2018/2/25.
 */
@Database(name = "testdb2")
@Table(name = "other", generate = GeneratorType.AUTO, engine = ModelEngineType.CACHE_MYSQL_OBJECT)
public class Other extends ModelObject<Other> {
    @Id
    @Column
    private Integer id;
    @Column
    private String desc;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }


}