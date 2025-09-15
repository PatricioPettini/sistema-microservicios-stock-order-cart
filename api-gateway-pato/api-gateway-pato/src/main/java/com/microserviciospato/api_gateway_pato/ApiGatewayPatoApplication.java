package com.microserviciospato.api_gateway_pato;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayPatoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayPatoApplication.class, args);
	}
}
