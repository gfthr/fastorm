package com.demo.mybatis_mongodb_mq_redis.controllers;

import com.demo.mybatis_mongodb_mq_redis.models.Mybatis;
import com.demo.mybatis_mongodb_mq_redis.models.User;
import com.demo.mybatis_mongodb_mq_redis.services.IMybatisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
            User user = User.create(new Object[]{13,2}, iniValue);
            user.save();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return mybatisService.list();
    }

    @RequestMapping("/get")
    public User get()
    {
        User user= null;
        try {
            user= User.fetch(new Object[]{12,2},false);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return user;
    }


}
