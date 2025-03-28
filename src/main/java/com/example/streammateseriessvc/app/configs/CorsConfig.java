package com.example.streammateseriessvc.app.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Позволява всички пътища
                .allowedOrigins("https://streammate-org.onrender.com")  // Разрешава само даден домейн
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Разрешава само определени методи
                .allowedHeaders("*");  // Позволява всички хедъри
    }
}
