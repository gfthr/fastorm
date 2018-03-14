package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelObject;
import com.chineseall.orm.annotations.*;
import java.util.List;
import java.util.Map;
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
    @Column
    private String name;
    @Column
    private List<String> config;
    @Column
    private Map<String,Object> map;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConfig() {
        return config;
    }

    public void setConfig(List<String> config) {
        this.config = config;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}