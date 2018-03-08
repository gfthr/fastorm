package com.chineseall.orm;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ModelProxy implements MethodInterceptor{
    private Enhancer enhancer = new Enhancer();
    private Object target = null;
    
    @SuppressWarnings("unchecked")
    public <E> E getProxyObject(Class<E> clasz){
        enhancer.setSuperclass(clasz);
        enhancer.setCallback(this);
        Object obj = enhancer.create();
        return (E)obj;
    }
    
    @SuppressWarnings("unchecked")
    public <E> E getProxyObject(Class<?> clasz, E target){
        this.target = target;
        enhancer.setSuperclass(clasz);
        enhancer.setCallback(this);
        Object obj = enhancer.create();
        return (E)obj;
    }
    
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable{
        Class<?> clasz = obj.getClass().getSuperclass();
        if (target != null){
            obj = target;
        }
        if (method.getName().startsWith("get")){
//            String fieldName = OrmInfo.getFieldName(method.getName());
//            OrmInfo orm = OrmInfo.getOrmInfo(clasz);

//            HasManyField hasManyField = orm.getHasManyField(fieldName);
//            if (hasManyField != null){
//                if (OrmInfo.getFieldValue(clasz, fieldName, obj) == null){
//                    Object idValue = OrmInfo.getFieldValue(clasz, orm.id, obj);
//                    Object items = ActiveRecordBase.findAll(hasManyField.getTargetType(), hasManyField.getForeignKey()+"=?", new Object[]{idValue}, hasManyField.getAnnotation().order());
//                    OrmInfo.setFieldValue(clasz, fieldName, obj, items);
//                }
//            }
//
//            HasOneField hasOneField = orm.getHasOneField(fieldName);
//            if (hasOneField != null){
//                if (OrmInfo.getFieldValue(clasz, fieldName, obj) == null){
//                    Object idValue = OrmInfo.getFieldValue(clasz, orm.id, obj);
//                    Object item = ActiveRecordBase.findFirst(hasOneField.getTargetType(), hasOneField.getForeignKey()+"=?", new Object[]{idValue}, hasOneField.getAnnotation().order());
//                    OrmInfo.setFieldValue(clasz, fieldName, obj, item);
//                }
//            }
//
//            BelongsToField belongsToField = orm.getBelongsToField(fieldName);
//            if (belongsToField != null){
//                if (OrmInfo.getFieldValue(clasz, fieldName, obj) == null){
//                    Object fkValue = OrmInfo.getFieldValue(clasz, belongsToField.getForeignKey(), obj);
//                    Object item = ActiveRecordBase.find(belongsToField.getTargetType(), fkValue);
//                    OrmInfo.setFieldValue(clasz, fieldName, obj, item);
//                }
//            }
        }else if(method.getName().startsWith("set")){
            ModelMeta meta = ModelMeta.getModelMeta(clasz);
            String fieldName = ModelMeta.getFieldName(method.getName());
            if(meta.columnSet.contains(fieldName)){
                Object oldFieldValue = ModelMeta.getFieldValue(clasz,fieldName,obj);
                Object newFieldValue = args[0];
                if(!oldFieldValue.equals(newFieldValue)){
                    if(obj instanceof Model){
                        ((Model) obj).getModified_attrs().add(fieldName);
                    }
                }
                System.out.print(">>>>:"+method.getName());
            }
        }
        
        if (target == null){
            return proxy.invokeSuper(obj, args);
        }
        else{
            return proxy.invoke(target, args);
        }
//        Object result = proxy.invokeSuper(obj, args);
//        return result;
    }
}
