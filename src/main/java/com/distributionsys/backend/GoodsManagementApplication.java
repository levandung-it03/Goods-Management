package com.distributionsys.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
@SpringBootApplication
public class GoodsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodsManagementApplication.class, args);
	}

}
