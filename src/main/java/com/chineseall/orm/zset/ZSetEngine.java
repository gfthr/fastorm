package com.chineseall.orm.zset;

/**
 * Created by wangqiang on 2018/3/19.
 */

import java.util.List;
import java.util.Map;

public abstract class ZSetEngine {

    public abstract void init_instance(ZSetModel instance);

    //将 value_pairs 里的数据添加到 zset 中
    public abstract void add(ZSetModel instance, Map<String, Double> value_pairs);

    //从 zset 中移除 values
    public abstract void remove(ZSetModel instance, String... member);

    //获取 zset 中 value 的 score
    public abstract Double score(ZSetModel instance, String value);

    /*:param offset: >= 0
        :param limit: -1 for all */
    public abstract List<ZSetValuePair> range(ZSetModel instance, int offset, int limit, boolean reverse);

    /* :param score_start: inclusive if not key_start, else exclusive
    :param score_end: inclusive
    :type limit: int
    :param key_start: exclusive，若不需指定则设置 '' 或 None */
    public abstract List<ZSetValuePair> scan(ZSetModel instance, String key_start, double score_start, double score_end, int limit,
                                             boolean reverse);

    public abstract long rank(ZSetModel instance, String value, boolean reverse);

    //获取 zset 里的 value 的数量
    public abstract long size(ZSetModel instance);

    public abstract long delete(ZSetModel instance);

    //检查是否需要 rebuild，若需要则 rebuild
    public abstract void check_rebuild(ZSetModel instance);

    public abstract void rebuild(ZSetModel instance);

}
