package com.ohgiraffers.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrBankApplication {

  public static void main(String[] args) {
    SpringApplication.run(HrBankApplication.class, args);
  }

}
