package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelValue;
import com.chineseall.orm.annotations.*;

/**
 * Created by wangqiang on 2018/3/22.
 */

@Database(name = "testdb2")
@Table(name = "book", generate = GeneratorType.NONE,engine = ModelEngineType.CACHE_MYSQL_VALUE ,column = "id")
public class UniqueBook extends ModelValue<UniqueBook> {
    @Id
    @Column
    private String uniqueId;

    @Column
    private Integer id;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
