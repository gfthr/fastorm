package com.chineseall.orm;

import com.chineseall.orm.exception.FastOrmException;
import com.chineseall.orm.proxy.ListProxy;
import com.chineseall.orm.proxy.MapProxy;
import com.chineseall.orm.storage.ModelEngine;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
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
        boolean isEnchanceListorMap =false;
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
                        ((Model) obj).markModified();
                    }
                }else if(oldFieldValue==null && newFieldValue!=null){
                    if(obj instanceof Model){
                        ((Model) obj).getModified_attrs().add(fieldName);
                        ((Model) obj).markModified();
                    }
                }
                System.out.print(">>>>:"+method.getName());
                //这里可以处理一下 list 和 map 给他们加上感知变化功能
                if(args!=null && args.length==1 && (args[0] instanceof List ||args[0] instanceof Map )){
                    isEnchanceListorMap = true;
                }
            }
        }

        Object[] newargs = args;
        if(isEnchanceListorMap){
            String key = ModelMeta.getFieldName(method.getName());
            if(args[0] instanceof List){
                ListProxy listProxy =new ListProxy(args[0],obj ,key);
                //创建代理类对象,newProxyInstance返回一个实现List接口的代理类对象
                List _proxy = (List) java.lang.reflect.Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{List.class}, listProxy);
                newargs[0] = _proxy;
            }else if(args[0] instanceof Map){
                MapProxy mapProxy =new MapProxy(args[0],obj ,key);
                Map _proxy = (Map) java.lang.reflect.Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Map.class}, mapProxy);
                newargs[0] = _proxy;
            }
        }
        
        if (target == null){
            return proxy.invokeSuper(obj, newargs);
        }
        else{
            return proxy.invoke(target, newargs);
        }
//        Object result = proxy.invokeSuper(obj, args);
//        return result;
    }
}
