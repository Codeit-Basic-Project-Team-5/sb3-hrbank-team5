package com.ohgiraffers.hrbank.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
    info = @Info(
        title = "HR Bank API",
        description = "HR Bank API 문서"),
    servers = @Server(
        url = "http://localhost:8080/sb/hrbank",
        description = "Generated server url")
)

@Configuration
public class SwaggerConfig {

}
