package com.perficient.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
@PropertySource("classpath:swagger2.properties")
public class SwaggerConfiguration {

    @Value("${swagger.basePackage}")
    private String basePackage;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.description}")
    private String description;

    @Value("${swagger.contact}")
    private String contact;

    @Value("${swagger.version}")
    private String version;

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(buildApiInf()).select()
            .apis(RequestHandlerSelectors.basePackage(basePackage)).paths(PathSelectors.any()).build();
    }

    private ApiInfo buildApiInf() {
        return new ApiInfoBuilder().title(title).description(description).contact(contact).version(version).build();
    }

}
