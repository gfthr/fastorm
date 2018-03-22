package com.chineseall.orm.zset;

/**
 * Created by wangqiang on 2018/3/19.
 */
public class RankZSetModel extends ZSetModel {

    public RankZSetModel(Object key, ZSetSource source, ZSetEngine engine, boolean check_rebuild, boolean score_ascending){
        super("key",source ,engine,key, check_rebuild);
        this.zset_score_ascending =score_ascending;
    }
}
