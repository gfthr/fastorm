package com.chineseall.orm.storage;

import com.chineseall.orm.Model;
import com.chineseall.orm.exception.ActiveRecordException;
import com.chineseall.orm.exception.OrmNotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class CacheEngine extends ModelEngine {
    private ModelEngine ng_source;
    private ModelEngine ng_cache;
    private Nonemark nonemark;

    public CacheEngine(Class model_class, ModelEngine ng_source, ModelEngine ng_cache, Nonemark nonemark) {
        super(model_class);
        this.ng_source = ng_source;
        this.ng_cache = ng_cache;
        this.nonemark = nonemark;
    }

    public static CacheEngine getMysqlObjectCacheEngine(Class model_class, String table, String delete_mark, String view,
                                                        ModelEngine ng_cache, Nonemark nonemark) {
        if (ng_cache == null) {
            ng_cache = new RedisEngine(model_class, 0);
        }
        if (nonemark == null) {
            nonemark = new RedisNonemark(0);
        }
        MysqlObjectEngine ng_source = new MysqlObjectEngine(model_class, table, delete_mark, view);
        return new CacheEngine(model_class, ng_source, ng_cache, nonemark);
    }

    public static CacheEngine getMysqlValueCacheEngine(Class model_class, String table, String column, String delete_mark, String view,
                                                       ModelEngine ng_cache, Nonemark nonemark) {
        if (ng_cache == null) {
            ng_cache = new RedisEngine(model_class, 0);
        }
        if (nonemark == null) {
            nonemark = new RedisNonemark(0);
        }
        MysqlValueEngine ng_source = new MysqlValueEngine(model_class, table, column, delete_mark, view);
        return new CacheEngine(model_class, ng_source, ng_cache, nonemark);
    }


    public Object fetch(Object[] key, boolean auto_create) throws ActiveRecordException {
        String general_key = this.model_class_gen_general_key(key);
        Object instance = null;
        // 先从缓存取。这里暂时不考虑 auto_create，如有需要再改这里的逻辑
        if (ng_cache != null) {
            instance = ng_cache.fetch(key, false);
            if (instance != null)
                return instance;
        }
        // 缓存没有，而且没有源，返回 None
        if (ng_source == null)
            return null;

        // 有源，不需 auto_create，检查 nonemark
        if (!auto_create) {
            // 有 nonemark，返回 None
            if (nonemark != null && nonemark.exists(general_key)) {
                return null;
            }
        }
        // 有源，且无 nonemark，从源取
        instance = ng_source.fetch(key, auto_create);

        if (instance == null) {
            // 不存在，设 nonemark
            if (nonemark != null) {
                nonemark.put(general_key, 0);
            } else {
                // 取到了，放缓存
                if (ng_cache != null)
                    ng_cache.save(instance);
            }
        }
        return instance;
    }


    public List<Object> fetchMulti(List<Object[]> keys) throws ActiveRecordException {
        List<Object> instances = new ArrayList<Object>();

        // 从缓存取
        if (ng_cache != null) {
            instances = ng_cache.fetchMulti(keys);
        }

        // 如果没有源，直接返回缓存的结果
        if (ng_source == null)
            return instances;

        // 有源时，把缓存里没命中的 key 取出来（剩余的 key），还有它的 index
        List<Object[]> rest_tuple_keys = new ArrayList<Object[]>();
        List<Integer> rest_indices = new ArrayList<Integer>();
        for (int i = 0; i < instances.size(); i++) {
            if (instances.get(i) == null) {
                rest_tuple_keys.add(keys.get(i));
                rest_indices.add(i);
            }
        }

        // 在剩余的 key 中去掉有 nonemark 的
        if (nonemark != null) {
            List<Object[]> exists_tuple_keys = new ArrayList<Object[]>();
            List<Integer> exists_indices = new ArrayList<Integer>();
            for (int i = 0; i < rest_tuple_keys.size(); i++) {
                if (!this.has_nonemark(rest_tuple_keys.get(i))) {
                    exists_tuple_keys.add(rest_tuple_keys.get(i));
                    exists_indices.add(rest_indices.get(i));
                }
            }
            rest_tuple_keys = exists_tuple_keys;
            rest_indices = exists_indices;

        }

        // 从源里取剩余的数据
        List<Object> rest_instances = ng_source.fetchMulti(rest_tuple_keys);
        for (int i = 0; i < rest_instances.size(); i++) {
            Object[] tuple_key = rest_tuple_keys.get(i);
            Integer index = rest_indices.get(i);
            Object instance = rest_instances.get(i);
            if (instance != null) {
                // 没取到，设 nonemark
                if (nonemark != null) {
                    nonemark.put(this.to_general_key(tuple_key), 0);
                } else {
                    // 取到了，设到结果里
                    instances.set(index, instance);
                    // 放缓存
                    if (ng_cache != null)
                        ng_cache.save(instance);
                }

            }
        }
        return instances;
    }

    public String to_general_key(Object[] tuple_key) throws ActiveRecordException {
        return this.model_class_gen_general_key(tuple_key);
    }


    public boolean has_nonemark(Object[] tuple_key) throws ActiveRecordException {
        return this.nonemark.exists(this.to_general_key(tuple_key));
    }


    public void save(Object instance) throws ActiveRecordException {
        if (!(instance instanceof Model)) {
            throw new ActiveRecordException("instance must be Model");
        }

        Model model = (Model) instance;

        if (ng_source != null) {
            //有源时，保存到源并从缓存中删除
            this.ng_source.save(instance);
            if (ng_cache != null) {
                this.ng_cache.delete(model.tuple_key());
            }

        } else {
            // 没有源时，才直接保存到缓存
            if (ng_cache != null)
                this.ng_cache.save(instance);
        }
        if (nonemark != null)
            this.nonemark.remove(model.general_key());
    }

    public void delete(Object[] key_values) throws ActiveRecordException {
        if (ng_cache != null)
            this.ng_source.delete(key_values);
        if (ng_cache != null)
            this.ng_cache.delete(key_values);
    }

    public void clear_cache(Object[] key) throws ActiveRecordException {
        if (ng_cache != null)
            this.ng_cache.delete(key);

        if (nonemark != null)
            this.nonemark.remove(this.to_general_key(key));
    }


}
