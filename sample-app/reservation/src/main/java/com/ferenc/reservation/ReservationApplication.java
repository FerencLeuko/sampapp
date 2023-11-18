package com.ferenc.reservation;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
public class ReservationApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReservationApplication.class, args);
	}

}
