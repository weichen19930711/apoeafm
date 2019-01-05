package com.perficient.library.common.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.perficient.library.common.converter.BookStatusConverter;
import com.perficient.library.common.converter.PurchaserConverter;
import com.perficient.library.web.interceptor.LoginInterceptor;
import com.perficient.library.web.interceptor.PermissionInterceptor;

@Configuration
public class CustomConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BookStatusConverter());
        registry.addConverter(new PurchaserConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("*").allowCredentials(true).allowedMethods("GET", "POST",
            "DELETE", "PUT", "OPTIONS");
        registry.addMapping("/login").allowedOrigins("*").allowCredentials(true).allowedMethods("GET", "POST");
        registry.addMapping("/logout").allowedOrigins("*").allowCredentials(true).allowedMethods("GET", "POST");
    }

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.getLoginInterceptor()).addPathPatterns("/api/**")
            .excludePathPatterns("/api/v1/login", "/api/v1/logout");
        registry.addInterceptor(this.getPermissionInterceptor()).addPathPatterns("/api/**")
            .excludePathPatterns("/api/v1/login", "/api/v1/logout");
    }

}
