package com.markendation.server;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.models.Product;
import com.markendation.server.repositories.secondary.ProductRepository;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	// @Bean
	// CommandLineRunner runner(ProductRepository productRepository) {
	// 	return args -> {
	// 		List<Product> products = productRepository.findAll();
	// 		for (Product product : products) {
	// 			System.out.println(product);
	// 		}
	// 	};
	// }

}
