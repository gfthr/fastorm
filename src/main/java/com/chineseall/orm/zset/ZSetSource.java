package com.chineseall.orm.zset;

import java.util.List;

/**
 * Created by wangqiang on 2018/3/19.
 */
public abstract class ZSetSource {
    //从数据源获取数据
    public abstract List<ZSetValuePair> load(ZSetModel instance);

    //从数据源获取数据的数量
    public abstract int count(ZSetModel instance);

    //直接从数据源获取数据
    public abstract List<Object> fetch(ZSetModel instance, int offset, int count);
}
