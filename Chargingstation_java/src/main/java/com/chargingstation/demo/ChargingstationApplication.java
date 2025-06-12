package com.chargingstation.demo;

import com.chargingstation.demo.CLI.ChargingStationCLI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChargingstationApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ChargingstationApplication.class, args);
		ChargingStationCLI cli = context.getBean(ChargingStationCLI.class);
		cli.run();


	}

}
