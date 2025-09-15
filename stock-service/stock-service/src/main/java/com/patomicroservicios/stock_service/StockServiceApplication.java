package com.patomicroservicios.stock_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.patomicroservicios.stock_service.repository") // o donde esté tu ProductAPI
@EnableDiscoveryClient
public class StockServiceApplication {

	public static void main(String[] args) {
		System.out.println("JWK_SET_URI env: " + System.getenv("JWK_SET_URI"));
		System.out.println(System.getenv("ISSUER_URI"));
		SpringApplication.run(StockServiceApplication.class, args);
	}

}
