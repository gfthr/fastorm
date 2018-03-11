package com.demo.mybatis_mongodb_mq_redis;

import com.chineseall.orm.ModelScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
public class MybatisMongodbMqRedisApplication {

	public static void main(String[] args) {
		ModelScanner.scanOrmModel("com.demo.mybatis_mongodb_mq_redis.models");
		SpringApplication.run(MybatisMongodbMqRedisApplication.class, args);
	}
}
