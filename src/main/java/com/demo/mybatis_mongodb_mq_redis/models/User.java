package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ModelObject;
import com.chineseall.orm.annotations.*;

/**
 * Created by wangqiang on 2018/2/25.
 */
@Database(name = "testdb1")
@Table(name = "users", generate = GeneratorType.AUTO,engine = ModelEngineType.CACHE_MYSQL_OBJECT)
public class User extends ModelObject<User> {
    @Id
    @Column
    private Integer id;
    @Id
    @Column
    private Integer aid;
    @Column
    private String name;
    @Column
    private String addr;
    @Column
    private String email;
    @Column(default_value = "")
    private String remark;
    @Column(default_value = "99")
    private Integer status;
    //get,set...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}