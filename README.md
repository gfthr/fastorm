# fastorm
Forget hibernate and mybatis it, fastOrm will achieve the best Java and Orm based on the fastest, for tens of millions of more than PV high concurrent Internet applications, highly integrated with Redis, using KV to achieve the object cache cache, query cache for Zset development, convenient and simple, debugging SQL statements are simple


忘记hibernate 和 mybatis吧，fastOrm 将实现最好和最快的基于java的Orm ，真正面向千万级PV以上高并发互联网应用，与Redis高度集成，用KV缓存来实现对象的缓存，用Zset 实现查询的缓存，开发方便调试SQL语句也简单简单，

现在的初步想法：

1）重度使用Redis作为缓存，所有对数据库的访问都必须通过缓存，用reidis 的 KV缓存来实现实体对象的缓存，用reidis 的 Zset 实现查询的缓存；缓存实现防穿透，防雪崩的机制

2）不支持一对多、一对一等关联关系，这类东西在高并发网站不需要

3）使用简单，配置简单，无xml文件，通过注解指定ID字段生成类型，通过注解指定乐观锁版本字段，通过注解指定删除标志字段

4）支持原生SQL语句，调优方便

5）实体对象的字段支持list 和map 等复杂类型，数据库里存为json格式的大文本

6）支持基于乐观锁的更新，支持删除标志的删除（假删除）

# 基本使用

* [example link](http://example.com/)

* [example link](http://example.com/)

