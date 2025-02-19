package com.lcode.demo_park_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Demo Park API")
                                .description("API para gestão de estacionamento de veículos")
                                .version("v1")
                                .license(new License().name("Apache 2.0").url("http://apache.org/licenses/LICENSE-2.0"))
                                .contact(new Contact().name("Luiz Gustavo").email("luizgustavolima200@gmail.com")));
    }

}
