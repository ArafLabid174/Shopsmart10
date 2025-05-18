package com.shopsmart.shopsmart_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ShopsmartServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopsmartServerApplication.class, args);
	}
}
