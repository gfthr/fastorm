package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.ActiveRecordBase;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;

/**
 * Created by wangqiang on 2018/2/25.
 */
public class BaseModel extends ActiveRecordBase {

    public void BaseModel(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(BaseModel.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello cglib";
            }
        });
        BaseModel proxy = (BaseModel) enhancer.create();
//        System.out.println(proxy.test(null)); //拦截test，输出Hello cglib
        System.out.println(proxy.toString());
        System.out.println(proxy.getClass());
        System.out.println(proxy.hashCode());
//        this = proxy;
    }
}
