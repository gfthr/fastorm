package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelObject;
import com.chineseall.orm.annotations.*;

/**
 * Created by wangqiang on 2018/3/22.
 */

@Database(name = "testdb2")
@Table(name = "book", generate = GeneratorType.AUTO,engine = ModelEngineType.CACHE_MYSQL_OBJECT)
public class Book extends ModelObject<Book> {
    @Id
    @Column
    private Integer id;
    @Column
    private String uniqueId;
    @Column
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
