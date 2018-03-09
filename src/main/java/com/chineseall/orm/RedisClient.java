package com.chineseall.orm;

/**
 * Created by wangqiang on 2018/3/7.
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class RedisClient {
    /** 非切片链接池 */
    private static JedisPool jedisPool;


    private synchronized static void initPool() {
        if (jedisPool == null) {

            InputStream inputStream = null;
            try {
                Properties prop = new Properties();
                inputStream = RedisClient.class.getClassLoader().getResourceAsStream("jedis.properties");
                prop.load(inputStream);
                String host = prop.getProperty("host");
                Integer port = Integer.parseInt( prop.getProperty("port"));

                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(500);
                poolConfig.setTestOnBorrow(true);
                poolConfig.setTestOnReturn(false);
                poolConfig.setMaxIdle(10);
                poolConfig.setMinIdle(1);
                poolConfig.setTestWhileIdle(true);
                poolConfig.setMaxWaitMillis(1000L);
                poolConfig.setNumTestsPerEvictionRun(10);
                poolConfig.setTimeBetweenEvictionRunsMillis(60000L);
                jedisPool = new JedisPool(poolConfig, host, port);

            } catch (IOException ioe) {
                ioe.printStackTrace();
//                logger.error("jedis.properties load error", ioe);
//                return null;
            } finally {
                if(inputStream != null) try { inputStream.close(); } catch (IOException ioe) {}
            }
        }
    }

    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    private static JedisPool getJedisPool() {
        if (jedisPool == null) {
            initPool();
        }
        return jedisPool;
    }

    public static void set(String key, Integer value) {
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            localJedis.set(key, value.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void set(String key, Object obj) {
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            localJedis.set(key.getBytes(), SerializeUtil.serialize(obj));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 从Redis里获取一个Object类型的Value
     * @param key
     * @return
     */
    public static Object get(byte[] key) {
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            byte[] value = localJedis.get(key);
            if(value==null){
                return null;
            }
            return SerializeUtil.unserialize(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从Redis里获取一个String类型的Value
     * @param key
     * @return
     */
    public static String get(String key) {
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            return localJedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 删除一个Redis Key
     * @param key
     */
    public static void remove(String key) {
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            localJedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static long incr(String key, int intvalue) {
        long value = 0;
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            value = localJedis.incrBy(key, intvalue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return value;
    }

    public static long incr(String key) {
        long value = 0;
        Jedis localJedis = null;
        try {
            localJedis = getJedisPool().getResource();
            value = localJedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localJedis != null) {
                try {
                    getJedisPool().returnResource(localJedis);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return value;
    }
}