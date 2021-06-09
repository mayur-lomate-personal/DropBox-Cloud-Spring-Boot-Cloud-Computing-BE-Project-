package com.sinhgad.cc.main;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.dropbox.core.DbxException;
import com.sinhgad.cc.controller.Services;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sinhgad.cc.controller"})
public class CloudComputingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudComputingApplication.class, args);
		
	}

}
