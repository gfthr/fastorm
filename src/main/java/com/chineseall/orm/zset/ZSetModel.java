package com.chineseall.orm.zset;

import com.chineseall.orm.utils.Setting;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wangqiang on 2018/3/19.
 */
public class ZSetModel {
    // nonemark
    private boolean noneMark;
    // 最后一次 check_rebuild 的时间
    private long lastCheckRebuildSec;

    protected String identifier = "";
    protected ZSetSource zset_source;
    protected ZSetEngine zset_engine;

    //score 的默认排序顺序为降序
    protected boolean zset_score_ascending = false;

    private Object[] keys;

    public ZSetModel(Object[] keys, boolean check_rebuild){
        this.zset_engine.init_instance(this);
        this.keys = keys;
        if(check_rebuild)
            this.zset_engine.check_rebuild(this);
    }

    public ZSetModel(Object key, boolean check_rebuild){
        this.zset_engine.init_instance(this);
        this.keys = new Object[]{key};
        if(check_rebuild)
            this.zset_engine.check_rebuild(this);
    }

    public boolean isNoneMark() {
        return noneMark;
    }

    public void setNoneMark(boolean noneMark) {
        this.noneMark = noneMark;
    }

    public long getLastCheckRebuildSec() {
        return lastCheckRebuildSec;
    }

    public void setLastCheckRebuildSec(long lastCheckRebuildSec) {
        this.lastCheckRebuildSec = lastCheckRebuildSec;
    }

    public String general_key(){
        return this.gen_general_key(this.getClass(), this.keys);
    }

    public Object[] tuple_key(){
        return this.keys;
    }

    public static String gen_general_key(Class<?> classz,  Object[] tuple_key){
        //生成 general key，子类可以按需覆盖此方法
        String key_str = StringUtils.arrayToDelimitedString(tuple_key,"|");
        return String.format("%s%s|%s", Setting.REAL_CACHE_LOCAL_PREFIX, classz.getName(),key_str);
    }

    public static ZSetModel get(Object[] keys, boolean check_rebuild){
        return new ZSetModel(keys, check_rebuild);
    }

    public void add(String value, Double score){
        Map<String,Double> map=new HashMap<>();
        map.put(value,score);
        this.zset_engine.add(this, map);
    }

    public void remove(String[] values){
        this.zset_engine.remove(this, values);
    }

    /*:param offset: >= 0
      :param limit: -1 for all
    */
    public List<Object> range(int offset, int limit, boolean reverse){
        List<ZSetValuePair> value_pairs = this.range_with_score(offset, limit, reverse);
        List<Object> values= new ArrayList<>();
        for (ZSetValuePair pair:value_pairs
             ) {
            values.add(pair.getValue());
        }
        return values;
    }

    /*:param offset: >= 0
      :param limit: -1 for all
    */
    public List<ZSetValuePair> range_with_score(int offset, int limit, boolean reverse){
        return this.zset_engine.range(this, offset, limit, reverse);
    }

    public List<Object> all(boolean reverse){
        return this.range(0, -1, reverse);
    }


    public Double score(String value){
        return this.zset_engine.score(this, value);
    }

    /* :param score_start: inclusive if not key_score, else exclusive
       :param score_end: inclusive
       :type limit: int
       :param key_start: exclusive，若不需指定则设置 '' 或 None
   */
    public List<ZSetValuePair> scan(String key_start, int score_start, int score_end, int limit, boolean reverse){
        return this.zset_engine.scan(this, key_start, score_start, score_end, limit, reverse);
    }

    public ZSetValuePair first(){
        List<ZSetValuePair> result = this.range_with_score(0, 1, false);
        return result!=null?result.get(0):null;
    }

    public ZSetValuePair last(){
        List<ZSetValuePair> result = this.range_with_score(0, 1, true);
        return result!=null?result.get(0):null;
    }

    //获取第一个的 score
    public Double first_score(){
        ZSetValuePair first = this.first();
        return first!=null?first.getScore():0;
    }

    //获取最后一个的 score
    public Double last_score(){
        ZSetValuePair last = this.last();
        return last!=null?last.getScore():0;
    }

    //获取 value 的排名
    public long rank(String value, boolean reverse){
        return this.zset_engine.rank(this, value, reverse);
    }

    public long size(){
        return this.zset_engine.size(this);
    }

    public long delete(){
        return this.zset_engine.delete(this);
    }

    // 检查是否需要 rebuild
    public boolean check_rebuild(){
        return this.zset_engine.check_rebuild(this);
    }

    public void rebuild(){
        this.zset_engine.rebuild(this);
    }

    //从数据源获取数据的方法，默认从 zset_source 中获取，子类可以覆盖本方法以定制数据
    public List<ZSetValuePair>  load_from_source(){
        return this.zset_source.load(this);
    }

    //从数据源获取原始数据的条数
    public int count_from_source(){
        return this.zset_source.count(this);
    }

    //从数据源获取原始数据，可分页
    public List<Object> fetch_from_source(int  offset, int count){
        return this.zset_source.fetch(this, offset, count);
    }
}
