package com.demo.mybatis_mongodb_mq_redis.models;

import com.chineseall.orm.zset.*;

/**
 * Created by wangqiang on 2018/3/22.
 */
public class OtherZset extends ZSetModel {

    private OtherZset(String identifier, ZSetSource source, ZSetEngine engine, Object key, boolean check_rebuild) {
        super(identifier,source, engine, key, check_rebuild);
    }

    public static OtherZset get(Object key, boolean check_rebuild){
        String identifier = "platform";
        String view = "SELECT id,rank FROM other WHERE platform=?";
        ZSetSource zset_source = new MysqlZSetSource("testdb2", view, "id", "rank", null, null);
        ZSetEngine zset_engine = RedisZSetEngine.redis_sourced_zset(0, 0, true);
        return new OtherZset(identifier, zset_source, zset_engine, key, check_rebuild);
    }

}
