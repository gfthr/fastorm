package com.chineseall.orm.storage;

import com.alibaba.fastjson.JSON;
import com.chineseall.orm.*;
import com.chineseall.orm.adapters.Adapter;
import com.chineseall.orm.connections.ConnectionProvider;
import com.chineseall.orm.exception.ActiveRecordException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 * 提供基本的 SQL 生成和数据库操作。
 */
public abstract class AbstractMysqlEngine<T> extends ModelEngine<T>{
    DbClient dbClient;
    protected String table;
    protected String view;
    protected String delete_mark;

    //连接提供者
    private static Map<String,ConnectionProvider> connections = new HashMap<String,ConnectionProvider>();
    //数据库适配器（方言）
    private static Map<String,Adapter> adapters = new HashMap<String,Adapter>();

    public AbstractMysqlEngine(Class<T> model_class, String table, String delete_mark, String view){
        super(model_class);
        if((!StringUtils.isEmpty(table) ||!StringUtils.isEmpty(delete_mark)) && !StringUtils.isEmpty(view)){
            new ActiveRecordException("both (table or delete_mark) and view are set").printStackTrace();
        }
        this.table = table;
        this.delete_mark = delete_mark;
        this.view = view;
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        dbClient =new DbClient(connections.get(meta.db));
    }

    static {
        try{
            DatabaseConfReader reader = new DatabaseConfReader();
            reader.init();
            connections = reader.getConnections();
            adapters = reader.getAdapters();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 取得数据库连接提供者
     * @param c 指定的数据库连接所绑定的类class
     * @return 连接提供者
     */
    public static ConnectionProvider getConnectionProvider(Class<?> c) {
        return connections.get(getBaseClassName(c));
    }

    /**
     * 设置数据库连接提供者
     * @param dbName 域基类，由它登记数据库连接信息
     * @param cp 连接提供者
     */
    public static void putConnectionProvider(String dbName, ConnectionProvider cp){
        connections.put(dbName, cp);
    }

    /**
     * 取得数据库适配器
     * @param c 指定的数据库连接所绑定的类class
     * @return 数据库适配器
     */
    public static Adapter getConnectionAdapter(Class<?> c){
        return adapters.get(getBaseClassName(c));
    }

    /**
     * 设置连接适配器
     * @param domainClassName 域基类，由它登记数据库连接信息
     * @param adapter 适配器
     */
    public static void putConnectionAdapter(String domainClassName, Adapter adapter){
        adapters.put(domainClassName, adapter);
    }

    private static String getBaseClassName(Class<?> c){
        String className = c.getCanonicalName();
        ConnectionProvider cp = connections.get(className);
        while (cp == null){
            c = c.getSuperclass();
            if (c == null) {
                return null;
            }
            className = c.getCanonicalName();
            cp = connections.get(className);
        }
        return className;
    }

    protected String _sql_table(){
        return "`"+this.table+"`";
    }

    protected String[] _key_column_names(){
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        return meta.get_key_column_names();
    }

    protected String[] _column_names(){
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        return meta.get_column_names();
    }

    protected String _sql_condition(){
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        String[]  keys= new String[meta.idFields.length];
        for (int i=0;i<meta.idFields.length;i++){
            keys[i] = " "+meta.idFields[i].getName()+"=? ";
        }
        return StringUtils.arrayToDelimitedString(keys,"AND");
    }

    protected String _sql_select_condition(){
        if (StringUtils.isEmpty(this.delete_mark)){
            return this._sql_condition();
        }
        //`key-column1`=%s AND`key-column2`=%s AND`delete_mark`=0
        return String.format("%s AND`%s`=0",this._sql_condition(), this.delete_mark);
    }

    protected String _gen_sql_columns(String[] names){
        //`column1`,`column2`
        String columns[] = new String[names.length];
        for (int i=0;i<names.length;i++){
            columns[i] = "`"+names[i]+"`";
        }
        return StringUtils.arrayToDelimitedString(columns,",");
    }

    protected String _sql_delete(){
        // UPDATE `table` SET `delete`=1 WHERE `column`=%s AND`column2`=%s
        // 或
        // DELETE FROM `table` WHERE `column`=%s AND`column2`=%s
        if (!StringUtils.isEmpty(this.delete_mark)){
            String sql_set_delete = "`"+this.delete_mark+"`=1" ;
            return String.format("UPDATE %s SET %s WHERE %s",this._sql_table(),sql_set_delete,this._sql_condition());
        }else{
            return String.format("DELETE FROM %s WHERE %s",this._sql_table(),this._sql_condition());
        }
    }

    protected String _sql_update(){
        // 因为 SET 部分要根据变化的属性来动态生成，所以这里要把 % 逆转换一下
        // UPDATE `table` SET %s WHERE `name1`=%%s AND`name2`=%%s
        String condition_stub = this._sql_condition().replace("%", "%%");
        return "UPDATE "+this._sql_table()+" SET %s WHERE "+ condition_stub;
    }

    protected String _sql_select(){
        // SELECT `column1`,`column2` FROM `table`
        // WHERE `column3`=%s AND`column4`=%s
        String[] select_columns = this._get_column_names_for_select_();
        return String.format("SELECT %s FROM %s WHERE %s", this._gen_sql_columns(select_columns), this._sql_table(), this._sql_select_condition());
    }

    protected String _sql_insert_with_key(){
        // INSERT INTO `table` (`key-column`,`value-column`) VALUES (%s,%s)
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        String[] key_column_names = _key_column_names();
        String[] value_column_names = this._get_column_names_for_insert_();
        // 约定：key 在前，value 在后
        String[] column_names = ArrayUtils.addAll(key_column_names,value_column_names);
        return this._gen_sql_insert(column_names);
    }

    protected String _sql_insert_without_key(){
        // INSERT INTO `table` (`value-column1`,`value-column2`) VALUES (%s,%s)
        String[]  value_column_names = this._get_column_names_for_insert_();
        return this._gen_sql_insert(value_column_names);
    }

    protected String _gen_sql_insert(String[] column_names){
        // INSERT INTO `table` (`column1`,`column2`) VALUES (%s,%s)
        String[] tmp = new String[column_names.length];
        for (int i = 0; i < column_names.length ; i++) {
            tmp[i] ="?";
        }
        String sql_value_stub = StringUtils.arrayToDelimitedString(tmp,",");
        return String.format("INSERT INTO %s (%s) VALUES (%s)", this._sql_table(), this._gen_sql_columns(column_names),sql_value_stub);
    }

    protected Map<String,Object> _fetch_row_(Object[] key) throws ActiveRecordException{
        List<Map<String,Object>> rows = null;

        if(!StringUtils.isEmpty(this.table)){
            rows = dbClient.select(this._sql_select(), key,0,0);
        }else if (!StringUtils.isEmpty(this.view)){
            rows = dbClient.select(this.view, key,0,0);
        }

        Map<String,Object> result_dict =null;
        if (rows==null){
            result_dict =null;
        }else if(rows.size()>1){
            throw  new ActiveRecordException("Multiple rows returned for fetch() query");
        }else{
            result_dict = rows.get(0);
        }
        return result_dict;
    }

    public T fetch(Object[] key, boolean auto_create) throws ActiveRecordException {
        Object result= null;
        try {
            Map<String,Object> result_row = this._fetch_row_(key);
            Object result_data =null;
            if(result_row!=null){
                result_data = this._row_to_value_(result_row);
            }
            if (result_data!=null) {
                return (T)model_class_create(key, result_data);
            }

            if (!auto_create)
                return null;

            // auto_create
            boolean insert_conflict = false; // mark if successful in save
            T instance = (T)model_class_create(key, null);

            try {
                this.save(instance);
            }catch (Exception e){
//                if e.args[0] != SQLErrorCode.ER_DUP_ENTRY:
                insert_conflict = true;
            }

            if (!insert_conflict){
                return instance; //successfully saved
            }
            //conflict in save, re-fetch instance
            result_row = this._fetch_row_(key);
            result_data =null;
            if(result_row!=null){
                result_data = this._row_to_value_(result_row);
            }
            if (result_data ==null){
                throw new ActiveRecordException("None in re-fetch auto_create instance");
            }
            return (T)model_class_create(key, result_data);

        }catch (Exception ex){
            throw new ActiveRecordException("fetch error :"+ex.getMessage());
        }
//        return result;
    }

    protected String _sql_multi_select_columns(){
        // `key-column1`,`key-column2`,`value-column1`,`value-column2`
        String[] key_columns = _key_column_names();
        String[] value_columns = this._get_column_names_for_select_();
        String[] columns =  ArrayUtils.addAll(key_columns, value_columns);
        return this._gen_sql_columns(columns);
    }

    protected String _sql_multi_select_by_in(){
        // SELECT `key-column`,`value-column` FROM `table`
        // WHERE `condition-column` IN (%s) AND`delete_mark`=0
        String[] key_columns = _key_column_names();
        String condition = "`"+key_columns[0]+"`IN(%s)" ;
        if(!StringUtils.isEmpty(this.delete_mark)){
            condition = String.format("%s AND`%s`=0",condition,this.delete_mark);
        }
        return String.format("SELECT %s FROM %s WHERE %s",this._sql_multi_select_columns(),this._sql_table(), condition);
    }

    protected String _sql_multi_select_within_union(){
        // SELECT `key-column1`,`key-column2`,`value-column` FROM `table`
        // WHERE `key-column1`=%s AND`key-column2`=%s AND`delete_mark`=0
        return String.format("(SELECT %s FROM %s WHERE %s)", this._sql_multi_select_columns(), this._sql_table(), this._sql_select_condition());
    }

    protected String _gen_sql_multi_select_by_union(int count){
        // (SELECT ...) UNION (SELECT ...)
        String[] sql_selects = new String[count];
        for (int i=0;i<count;i++){
            sql_selects[i] = this._sql_multi_select_within_union();
        }
        return StringUtils.arrayToDelimitedString(sql_selects, " UNION ALL ");
    }

    protected List<Map<String,Object>> _fetch_rows_(List<Object[]> tuple_keys) throws ActiveRecordException {

        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        int count = tuple_keys.size();
        if (count==0){
            return rows;
        }

        if(!StringUtils.isEmpty(this.view)){
            //对于视图形式的查询，无法一次性从数据库查出，使用循环来替代
            String sql = this.view;
            for (Object key :tuple_keys){
                Object[] tuple_key=(Object[])key;
                List<Map<String,Object>> _rows = null;
                _rows = dbClient.select(this._sql_select(), tuple_key,0,0);
                if(_rows.size()>1){
                    throw new ActiveRecordException("Multiple rows returned for fetch() query");
                }
                rows.add(_rows.get(0));
            }
            return rows;
        }
        String[] key_columns = _key_column_names();

        if ((key_columns.length) == 1){
            Object[] sql_params =new Object[tuple_keys.size()];
            for (int i=0;i<tuple_keys.size();i++) {
                sql_params[i] = tuple_keys.get(i)[0];
            }

            String[] sql_in_array = new String[count];
            for (int i=0;i<count;i++){
                sql_in_array[i] = "%s";
            }
            String sql_in = StringUtils.arrayToDelimitedString(sql_in_array,",");
            String sql = String.format(this._sql_multi_select_by_in(),sql_in);
            rows = dbClient.select(sql, sql_params,0,0);
        }else{
            Object[] sql_params = new Object[count*key_columns.length];
            for (int i=0;i<tuple_keys.size();i++) {
                for (int j = 0; j < key_columns.length; j++) {
                    sql_params[i+j] = tuple_keys.get(i)[j];
                }
            }
            String sql = this._gen_sql_multi_select_by_union(count);
            rows = dbClient.select(sql, sql_params,0,0);
        }
        // 将 rows 按 tuple_keys 排序
//        extract_key = lambda row: tuple(row[name] for name in identifier)
//        rows_dict = {extract_key(row): row for row in rows}
//        rows = [rows_dict.get(tuple_key) for tuple_key in tuple_keys]
        return rows;
    }


    public void save(Object instance) throws ActiveRecordException{
        if(!StringUtils.isEmpty(this.view)){
            throw new ActiveRecordException("put unsaved entity to view");
        }
        if(!(instance instanceof Model)){
            throw new ActiveRecordException("instance must be Model");
        }

        int row_count=0 ;
        Model model =(Model)instance;

        String[] key_columns = _key_column_names();
        Object[] tuple_key = model.tuple_key();

        if (model.isModel_saved()){
            //该实体在数据库中已有对应数据，将更新过的属性保存到数据库
            Object[] column_result = this._get_columns_for_update_(model);
            String[] column_names = (String[])column_result[0];
            Object[] column_values= (Object[])column_result[1];

            if (column_names ==null)
                throw new ActiveRecordException("empty update_columns for put()");

            String[] sql_set =new String[column_names.length];
            for (int i=0;i<column_names.length;i++){
                sql_set[i] =  "`"+column_names[i]+"`=%s";
            }
            String str_sql_set = StringUtils.arrayToDelimitedString(sql_set,",");
            // UPDATE `table` SET `column1`=%s,`column2`=%s
            // WHERE `key1`=%s AND`key2`=%s
            String sql_update = String.format(this._sql_update(),str_sql_set);
            Object[] values =  ArrayUtils.addAll(column_values, tuple_key);
            row_count = dbClient.execute(sql_update,values);

        }else{
            ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
            //在数据库中并未有对应的数据，执行插入操作
            Object[] column_values = this._get_column_values_for_insert_(model);

            if(key_columns.length==1 && tuple_key[0]==null){
                //只有单个 key 且为空，由数据库生成 key 并设回实体中
                row_count = dbClient.execute(this._sql_insert_without_key(),column_values);

                Object last_id = dbClient.executeScalar("select last_insert_id()", null);
                Object id = ConvertUtil.castFromObject(last_id.toString(), meta.idFields[0].getType());

                meta.setFieldValue(this.model_class,meta.idFields[0].getName(),model,id);
            } else {
                // 有多个 key，或者有指定 key 的值，把 key 和属性直接插入数据库
                // 注：虽然这里支持多个 key 的插入，但不能用这些 key 作为主键
                // 表里必须要有额外的主键
                Object[] values =  ArrayUtils.addAll(tuple_key, column_values);
                row_count=  dbClient.execute(this._sql_insert_with_key(),values);
            }
        }
        if (row_count != 1)
            throw new ActiveRecordException("affect "+row_count+" rows in put-insert");
    }

    public void delete(Object[] key_values) throws ActiveRecordException{
        if(!StringUtils.isEmpty(this.view)){
            return;
        }
        try {
            dbClient.execute(this._sql_delete(),key_values);
        }catch (Exception ex){
            throw new ActiveRecordException("delete error:"+ ex.getMessage());
        }

    }

    public static Object[] __dump_values(Model model, String[] attr_names){
        //按 attr_names 的顺序 dump 出 instance 的属性，并把 dict，list 转成字符串
        Map<String,Object> result_dict = model.dump(attr_names);

        Object[] result_list = new Object[attr_names.length];

        Object value=null;
        for (int i = 0; i < attr_names.length; i++) {
            String attr_name = attr_names[i];
            value = result_dict.get(attr_name);
            if(value instanceof Map || value instanceof List){
                value = JSON.toJSONString(value);
            }
            result_list[i]=value;
        }
        return result_list;
    }

    protected abstract String[] _get_column_names_for_select_();

    protected abstract Object[] _get_columns_for_update_(Model model);

    protected abstract String[] _get_column_names_for_insert_();

    protected abstract Object[] _get_column_values_for_insert_(Model model);

    protected abstract Object _row_to_value_( Map<String,Object> row_dict);

}
