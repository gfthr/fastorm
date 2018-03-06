package com.demo.mybatis_mongodb_mq_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
public class MybatisMongodbMqRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisMongodbMqRedisApplication.class, args);
	}
}
