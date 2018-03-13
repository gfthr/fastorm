package com.chineseall.orm;

import com.chineseall.orm.exception.FastOrmException;
import com.chineseall.orm.storage.ModelEngine;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ModelProxy implements MethodInterceptor{
    private Enhancer enhancer = new Enhancer();
    private Object target = null;
    private static Map<Class<?>,ModelEngine> modelEngines;

    static {
        modelEngines = new HashMap<Class<?>,ModelEngine>();
    }

    public static void pushModelEngine(Class<?> classz, ModelEngine engine){
        modelEngines.put(classz, engine);
    }

    public static ModelEngine getModelEngine(Class<?> classz){
        return modelEngines.get(classz);
    }
    
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
        if (method.getName().startsWith("getModelEngine")){
            ModelEngine engine = modelEngines.get(clasz);
            if(engine!=null)
                return engine;
            else
                throw new FastOrmException(clasz.getName() + " ModelEngine not config!");
        }else if(method.getName().startsWith("set")){
            ModelMeta meta = ModelMeta.getModelMeta(clasz);
            String fieldName = ModelMeta.getFieldName(method.getName());
            if(meta.columnSet.contains(fieldName)){
                Object oldFieldValue = ModelMeta.getFieldValue(clasz,fieldName,obj);
                Object newFieldValue = args[0];
                if(oldFieldValue!=null && !oldFieldValue.equals(newFieldValue)){
                    if(obj instanceof Model){
                        ((Model) obj).getModified_attrs().add(fieldName);
                    }
                }else if(oldFieldValue==null && newFieldValue!=null){
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
