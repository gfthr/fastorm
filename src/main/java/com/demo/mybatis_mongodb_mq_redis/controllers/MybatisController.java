package com.demo.mybatis_mongodb_mq_redis.controllers;

import com.chineseall.orm.utils.DbClient;
import com.chineseall.orm.zset.ZSetValuePair;
import com.demo.mybatis_mongodb_mq_redis.models.*;
import com.demo.mybatis_mongodb_mq_redis.services.IMybatisService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("test")
public class MybatisController {
    private static Log log = LogFactory.getLog("MybatisController");
    @Resource
    private IMybatisService mybatisService;

    @RequestMapping("/getlist")
    public List<Mybatis> getList()
    {
        try {
            Map<String, Object> iniValue=new HashMap<String, Object>();
            iniValue.put("name", "name1");
            iniValue.put("addr", "addr1");
            User user = User.create(User.class, new Object[]{13,2}, iniValue);
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
            user= User.fetch(User.class, new Object[]{12,2},false);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return user;
    }

    @RequestMapping("/auto")
    public Auto auto()
    {
        Auto auto = null;
        try {
            auto = Auto.create(Auto.class, null,null);
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
        Other other = null;
        try {
            Random r = new Random();
            other = Other.create(Other.class, null,null);
            other.setDesc("good:add" + System.currentTimeMillis());
            other.setName("name1");
            List<String> list =new ArrayList<>();
            list.add("list1");
            list.add("list2");
            other.setConfig(list);
            Map<String,Object> map =new HashMap<>();
            map.put("key1","key1value");
            map.put("key2","key2value");
            other.setMap(map);
            other.setPlatform(r.nextInt(5));
            other.setRank(r.nextInt(100));
            other.save();
            System.out.print("Other "+other.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return other;
    }

    @RequestMapping("/otheredit")
    public Other otherEdit()
    {
        Other other = null;
        try {
            other = Other.fetch(Other.class,  new Object[]{5}, false);
            other.setDesc("good-otheredit:"+ System.currentTimeMillis());
            other.getConfig().add("test3");
            other.save();
            System.out.print("Other "+other.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return other;
    }

    @RequestMapping("/otherdelete")
    public Other otherDelete()
    {
        Other other = null;
        try {
            DbClient dbClient=new DbClient("testdb2");
            List<Map<String,Object>> result = dbClient.query("select id from other order by id asc",null,1,0);
            if(result!=null && result.size()>0){
                int delete_id=(Integer)result.get(0).get("id");
                if(delete_id>0){
                    other = Other.fetch(Other.class,  new Object[]{delete_id}, false);
                    other.delete();
                    System.out.print("Other "+other.getId());
                }
            }

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
            others = Other.fetchMulti(Other.class,keys );
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return others;
    }

    @RequestMapping("/otherzset")
    public List<Object> otherZSet()
    {
        OtherZset zset=OtherZset.get("1",true);
        List<Object> list = zset.all(false);
        log.error("list: "+list);

        ZSetValuePair first = zset.first();
        log.error("first:  "+first.getValue()+":"+first.getScore());

        ZSetValuePair last = zset.last();
        log.error("last:  "+last.getValue()+":"+last.getScore());

        long size1= zset.size();
        log.error("size1 :  "+size1);

        zset.add("25", 47.0);
        List<Object> listadd = zset.all(false);
        log.error("listadd:  "+listadd);

        Double first_score = zset.first_score();
        log.error("first_score:  "+first_score);

        Double last_score = zset.last_score();
        log.error("last_score:  "+last_score);

        long rank = zset.rank("12", false);
        log.error("rank 12:  "+rank);

        long size= zset.size();
        log.error("size :  "+size);

        List<ZSetValuePair> alldatas = zset.range_with_score(0,8,false);
        log.error("alldatas :"+alldatas.size());
        for (ZSetValuePair pair:
                alldatas) {
            log.error("alldatas :  "+pair.getValue()+" --- "+pair.getScore());
        }

        log.error("score 12 "+ zset.score("12"));

        zset.rebuild();
        List<Object> listrebuild = zset.all(false);
        log.error("listrebuild:  "+ listrebuild);

        //获得特定值前后的数据
        List<ZSetValuePair> allscans =  zset.scan("12",Double.MAX_VALUE,0,2,false);
        log.error("allscans :"+allscans.size());
        for (ZSetValuePair pair:
                allscans) {
            log.error("allscans :  "+pair.getValue()+" --- "+pair.getScore());
        }

        List<ZSetValuePair> allscansAsc =  zset.scan("12",0,Double.MAX_VALUE,2,true);
        log.error("allscansAsc :"+allscansAsc.size());
        for (ZSetValuePair pair:
                allscansAsc) {
            log.error("allscansAsc :  "+pair.getValue()+" --- "+pair.getScore());
        }

        zset.delete();
        List<Object> listdel = zset.all(false);
        log.error("listdel:  "+listdel);

        return list;
    }



}
