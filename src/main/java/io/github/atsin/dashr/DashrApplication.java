package io.github.atsin.dashr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DashrApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashrApplication.class, args);
	}

}
