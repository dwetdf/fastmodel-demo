package com.slaveselfcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SlaveSelfCheck {

	public static void main(String[] args) {
        SpringApplication.run(SlaveSelfCheck.class, args);
    }

}
