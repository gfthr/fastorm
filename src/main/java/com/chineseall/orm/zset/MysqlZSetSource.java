package com.chineseall.orm.zset;

import com.chineseall.orm.utils.DbClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/20.
 */
public class MysqlZSetSource extends ZSetSource{
    private static Log log = LogFactory.getLog("MysqlZSetSource");
    DbClient dbClient;
    private String view;
    private String view_count;
    private String view_fetch;
    private String value_column;
    private String score_column;

    public MysqlZSetSource(String db, String view, String value_column, String score_column,
                 String view_count ,String view_fetch){
        this.view = view;
        this.value_column = value_column;
        this.score_column = score_column;
        this.view_count = view_count;
        this.view_fetch = view_fetch;
        dbClient =new DbClient(db);
    }

    public List<ZSetValuePair> load(ZSetModel instance)  {
        Object[] params = this.__get_params(instance);
        List<ZSetValuePair> datas=new ArrayList<>();
        try {
            List<Map<String,Object>> rows = dbClient.query(this.view, params,0,0);
            for (Map<String,Object> map:
                    rows) {
                String value=""+ map.get(value_column);
                Double score=Double.parseDouble(""+map.get(score_column));
                datas.add(new ZSetValuePair(value, score));
            }

        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return datas;
    }

    //从数据源获取数据的数量
    public int count(ZSetModel instance){
        int countvalue=0;
        Object[] params = this.__get_params(instance);
        try {
            countvalue=dbClient.count(this.view_count, params);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return countvalue;
    }

    //直接从数据源获取数据
    public List<Object> fetch(ZSetModel instance, int offset, int count){
        Object[] params = this.__get_params(instance);
        List<Object> datas=new ArrayList<>();
        try {
            List<Map<String,Object>> rows = dbClient.query(this.view_fetch, params,offset,count);
            for (Map<String,Object> map:
                    rows) {
                Object value=map.get(value_column);
                datas.add(value);
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return datas;
    }

    public Object[] __get_params(ZSetModel instance){
        return instance.tuple_key();
    }
}
