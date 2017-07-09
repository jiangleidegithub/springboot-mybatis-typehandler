package com.jianglei.mstransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jianglei.mstransfer.dao.CommonDao;

@SpringBootApplication
public class MsTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransferApplication.class, args);
	}
	
}
