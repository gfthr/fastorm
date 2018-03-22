package com.chineseall.orm.zset;

import com.chineseall.orm.storage.Nonemark;
import com.chineseall.orm.storage.RedisNonemark;
import com.chineseall.orm.utils.RedisClient;
import com.chineseall.orm.utils.Setting;
import redis.clients.jedis.Tuple;

import java.util.*;

import static com.chineseall.orm.utils.RedisClient.getResource;

/**
 * Created by wangqiang on 2018/3/20.
 */
public class RedisZSetEngine {
    /*
    对于一个实体的 <CacheKey>，在 redis 里会有以下相关存储。
    <CacheKey>|LastRebuildSec = <TimestampSec>
    <CacheKey>: (zset)
        <Value> = <Score> (value-score pairs)
     */
    public static Nonemark noneMark= new RedisNonemark(0);

    private int expire_sec;
    private int rebuild_sec;
    private boolean await_rebuild;

//    private static final String __ATTR_NONEMARK = "_ZSet__Nonemark";
//    private static final String __ATTR_LAST_CHECK_REBUILD_SEC = "_ZSet__LastCheckRebuildSec";
    private static final int __CHECK_REBUILD_INTERVAL_SEC = 1; //在这个时间段内不去 redis check_rebuild
    private static final int __MAX_TRY_REBUILD_TIMES = 10;  // rebuild 时拿不到锁后的等待次数
    private static final int __TRY_REBUILD_SLEEP_SEC = 1; // rebuild 时拿不到锁后每次等待的时间

    public RedisZSetEngine(int expire_sec, int rebuild_sec, boolean await_rebuild){
        //rebuild 时若有其它进程在 rebuild，等待或者跳过
        this.expire_sec = expire_sec;
        this.rebuild_sec = rebuild_sec;
        this.await_rebuild = await_rebuild;
    }

    public static RedisZSetEngine redis_sourced_zset(int expire_sec, int rebuild_sec , boolean await_rebuild){
        if (expire_sec<=0){
            Random r = new Random();
            int sec = (int) Math.floor(Setting.zset_expired_seconds*(1.6+r.nextFloat()));
            expire_sec = sec;
        }
        if (rebuild_sec<=0){
            Random r = new Random();
            int sec = (int) Math.floor(Setting.zset_rebuild_seconds*(1.6+r.nextFloat()));
            rebuild_sec = sec;
        }
        return new RedisZSetEngine(expire_sec, rebuild_sec, await_rebuild);
    }

    public void init_instance(ZSetModel instance){
        // 在实例里记录一些信息，避免频繁操作缓存
        instance.setNoneMark(false);
        instance.setLastCheckRebuildSec(0);
    }

    public void add(ZSetModel instance, Map<String, Double> value_pairs){
        getResource().zadd(instance.general_key(), value_pairs);
        this._data_modified(instance);
    }

    public void remove(ZSetModel instance, String... member){
        RedisClient.getResource().zrem(instance.general_key(), member);

    }

    public Double score(ZSetModel instance,String value){
        this.check_rebuild(instance);
        Double score = getResource().zscore(instance.general_key(), value);
        return score;
    }

    public List<ZSetValuePair> range(ZSetModel instance, int offset, int limit, boolean reverse){
        this.check_rebuild(instance);
        if (offset < 0 || limit == 0)
            return new ArrayList<ZSetValuePair>();

        int start = offset;
        int end = (limit < 0)?-1:(offset + limit - 1);

        boolean ascending = instance.zset_score_ascending ^ reverse;
        Set<Tuple> values =null;
        if(ascending){
            values= RedisClient.getResource().zrangeWithScores(instance.general_key(),start, end);
        }else{
            values= RedisClient.getResource().zrevrangeWithScores(instance.general_key(),start, end);
        }
        return this.__load_value_pairs(values);
    }

    public List<ZSetValuePair> scan(ZSetModel instance, String key_start, double score_start, double score_end, int limit,
                                             boolean reverse){
        this.check_rebuild(instance);
        if(key_start!=null){
            return this.__scan_with_key(
                    instance, key_start, score_start, score_end, limit, reverse);
        }
        return this.__scan_without_key(
                instance, score_start, score_end, limit, reverse);
    }

    public long rank(ZSetModel instance, String value, boolean reverse){
        this.check_rebuild(instance);
        long rank_result = 0;
        boolean ascending = instance.zset_score_ascending ^ reverse;
        if(ascending){
            rank_result = RedisClient.getResource().zrank(instance.general_key(), value);
        }else{
            rank_result = RedisClient.getResource().zrevrank(instance.general_key(), value);
        }
        return rank_result;
    }

    public long size(ZSetModel instance){
        this.check_rebuild(instance);
        return  RedisClient.getResource().zcard(instance.general_key());
    }

    public long delete(ZSetModel instance){
        RedisClient.getResource().del(instance.general_key());
        noneMark.remove(instance.general_key());
        return RedisClient.getResource().del(this._get_last_rebuild_sec_key(instance));
    }

    //按需 rebuild
    public void check_rebuild(ZSetModel instance){
        //如果距离上次 check_rebuild 的时间很短，就不再操作，避免频繁访问缓存
        long last_sec = instance.getLastCheckRebuildSec();
        long now = System.currentTimeMillis()/1000;
        if(now - last_sec < __CHECK_REBUILD_INTERVAL_SEC){
            return;
        }else{
            instance.setLastCheckRebuildSec(now);
        }
        //距离上次 check_rebuild 有一段时间了，重新去 redis 里 check 一下
        if (!this._need_rebuild(instance)){
            return;
        }
        this.rebuild(instance);
    }

    //通过缓存中的各标识判断是否需要 rebuild
    private boolean _need_rebuild(ZSetModel instance){
        String cache_key = instance.general_key();
        //缓存中不存在数据
        if (!RedisClient.getResource().exists(cache_key)){
            // 判断是否是 None
            if(noneMark.exists(cache_key)){
                instance.setNoneMark(true);
                return false;
            }
            instance.setNoneMark(false);
            return true;
        }
        //缓存中有数据
        instance.setNoneMark(false);
        // 若最后 rebuild 的时间距现在超过更新间隔，则需要 rebuild
        String last_rebuild_sec_key = this._get_last_rebuild_sec_key(instance);
        long last_rebuild_sec =Long.parseLong(RedisClient.getResource().get(last_rebuild_sec_key)) ;

        long now = System.currentTimeMillis()/1000;

        if (now - last_rebuild_sec >= this.rebuild_sec){
            return true;
        }
        return false;
    }

    public void rebuild(ZSetModel instance){
        String cache_key = instance.general_key();

        List<ZSetValuePair> value_pairs = instance.load_from_source();

        if (value_pairs==null || (value_pairs!=null&& value_pairs.size()==0)){
            //没有数据，清掉缓存中的内容
            RedisClient.getResource().del(cache_key);
            // 设置 NoneMark
            noneMark.put(cache_key, 0);
            instance.setNoneMark(true);
            RedisClient.getResource().del(this._get_last_rebuild_sec_key(instance));
            return;
        }


        // 有数据的情况
        RedisClient.getResource().del(cache_key);
        RedisClient.getResource().zadd(cache_key,__list_to_map(value_pairs));
        RedisClient.getResource().expire(cache_key, this.expire_sec);

        // 清 NoneMark
        noneMark.remove(cache_key);
        instance.setNoneMark(false);

        // 记录 LastRebuildSec
        long now_sec = System.currentTimeMillis()/1000;
        String last_rebuild_sec_key = this._get_last_rebuild_sec_key(instance);
        RedisClient.getResource().setex(last_rebuild_sec_key, this.expire_sec , ""+now_sec);
    }

    //往有 none mark 的实例里写数据后，需要清 none mark，并设置新的标识
    public void _data_modified(ZSetModel instance){

        if (!instance.isNoneMark()){
            //实例里没有记录 nonemark，说明缓存中有数据
            return;
        }
        // 实例里有记录 nonemark，说明缓存中没有数据，需要在缓存中设置相应标识
        //cache_key's expire
        String cache_key = instance.general_key();
        RedisClient.getResource().expire(cache_key, this.expire_sec);
        //last_rebuild_sec
        long now = System.currentTimeMillis()/1000;
        String last_rebuild_sec_key = this._get_last_rebuild_sec_key(instance);
        RedisClient.getResource().setex(last_rebuild_sec_key, this.expire_sec , ""+now);
        // 清 none_mark
        noneMark.remove(cache_key);
        instance.setNoneMark(false);
    }


    private List<ZSetValuePair>  __scan_without_key(ZSetModel instance, double score_start, double score_end, int limit,
                                                    boolean reverse){

        boolean ascending = instance.zset_score_ascending ^ reverse;
        Set<Tuple> values =null;
        if(ascending){
            values= RedisClient.getResource().zrangeByScoreWithScores(instance.general_key(),score_start, score_end,0,limit);
        }else{
            values= RedisClient.getResource().zrevrangeByScoreWithScores(instance.general_key(),score_start, score_end,0,limit);
        }
        return this.__load_value_pairs(values);
    }

    /* TODO 此函数 不同于 riceball的函数 需要验证一下 ,实现在一个评分区间内,获得特定值前后的记录*/
    private List<ZSetValuePair> __scan_with_key(ZSetModel instance, String key_start, double score_start, double score_end, int limit,
                                                boolean reverse){
        boolean ascending = instance.zset_score_ascending ^ reverse;
        Set<Tuple> values =null;

        Double key_start_score = RedisClient.getResource().zscore(instance.general_key(), key_start);
        if(key_start_score==null){
            return new ArrayList<>();
        }

        if(ascending){
            double start = key_start_score>score_start?key_start_score:score_start;
            values= RedisClient.getResource().zrangeByScoreWithScores(instance.general_key(),start, score_end,0,limit);
        }else{
            double end = key_start_score<score_end?key_start_score:score_end;
            values= RedisClient.getResource().zrevrangeByScoreWithScores(instance.general_key(),score_start, end,0,limit);
        }
        if(values==null)
            return new ArrayList<>();
        return this.__load_value_pairs(values);
    }

    private List<ZSetValuePair> __load_value_pairs(Set<Tuple> values){
        List<ZSetValuePair> valuePairs= new ArrayList<ZSetValuePair>();
        for (Tuple t:
                values) {
            ZSetValuePair value=new ZSetValuePair(t.getElement(),t.getScore());
            valuePairs.add(value);
        }
        return valuePairs;
    }

    private static String _get_last_rebuild_sec_key(ZSetModel instance){
        return get_subkey(instance, "LastRebuildSec", true);
    }

    private static String get_subkey(ZSetModel instance, String sub_type, boolean cached){
        /*
        将 sub_type 附加到 instance 的 general_key 后作为子 key。
        若 cached=True，结果会缓存到 instance 里。
        这是个给 RedisZSetEngine 和 SsdbZSetEngine 用的工具函数。

                :type instance: XSortedSet
        :type sub_type: object
        :rtype: str
        */
        return  String.format("%s|%s",instance.general_key(), sub_type);
    }

    private Map<String,Double> __list_to_map(List<ZSetValuePair> valuePairs){
        Map<String,Double> map=new HashMap<>();
        for (ZSetValuePair t:
                valuePairs) {
            map.put(t.getValue(),t.getScore());
        }
        return map;
    }

}
