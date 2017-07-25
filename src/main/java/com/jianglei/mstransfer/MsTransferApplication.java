package com.jianglei.mstransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.jianglei.mstransfer.datasource.DynamicDataSourceRegister;
@Import(DynamicDataSourceRegister.class)
@SpringBootApplication
public class MsTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransferApplication.class, args);
	}
	
}
