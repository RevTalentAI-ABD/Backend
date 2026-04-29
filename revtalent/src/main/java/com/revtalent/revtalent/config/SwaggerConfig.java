package com.revtalent.revtalent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI managerModuleAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RevTalent - Manager Module API")
                        .description("REST APIs for the Manager module — attendance, leaves, team, notifications, dashboard")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RevTalent Dev Team")
                                .email("dev@revtalent.com")));
    }
}