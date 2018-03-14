package com.demo.mybatis_mongodb_mq_redis.controllers;

import com.demo.mybatis_mongodb_mq_redis.models.Auto;
import com.demo.mybatis_mongodb_mq_redis.models.Mybatis;
import com.demo.mybatis_mongodb_mq_redis.models.Other;
import com.demo.mybatis_mongodb_mq_redis.models.User;
import com.demo.mybatis_mongodb_mq_redis.services.IMybatisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("test")
public class MybatisController {

    @Resource
    private IMybatisService mybatisService;

    @RequestMapping("/getlist")
    public List<Mybatis> getList()
    {
        try {
            Map<String, Object> iniValue=new HashMap<String, Object>();
            iniValue.put("name", "name1");
            iniValue.put("addr", "addr1");
            User user = new User().create(new Object[]{13,2}, iniValue);
            user.save();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return mybatisService.list();
    }

    @RequestMapping("/get")
    public User get()
    {
        User user= new User();
        try {
            user= user.fetch(new Object[]{12,2},false);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return user;
    }

    @RequestMapping("/auto")
    public Auto auto()
    {
        Auto auto = new Auto();
        try {
            auto = auto.create(null,null);
            auto.setName("good");
            auto.save();
            System.out.print("Auto "+auto.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return auto;
    }

    @RequestMapping("/other")
    public Other other()
    {
        Other other = new Other();
        try {
            other = other.create(null,null);
            other.setDesc("good");
            other.save();
            System.out.print("Other "+other.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return other;
    }

    @RequestMapping("/othermulti")
    public List<Other>otherMulti()
    {
        List<Other> others = new ArrayList<Other>();
        try {
            List<Object[]> keys=new ArrayList<Object[]>();
            keys.add(new Object[]{1});
            keys.add(new Object[]{2});
            keys.add(new Object[]{3});
            Other other =new Other();
            others = other.fetchMulti(keys);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return others;
    }



}
