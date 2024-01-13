package com.vikram.blogapp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfig {

    @Bean
    public OpenAPI apiDocConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("BLOG APP")
                        .description("API Documentation for BLOG APP")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("vikram")
                                .email("vikram.bedi97ca@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation")
                        .url("https://github.com/SinghVikram97"));
    }

}
