package com.amusement.amusement_park.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // -> Spring annotation, tells the framework: “This class defines configuration beans.
public class CorsConfig { //We are making a Spring configuration class called CorsConfig.

    @Bean
    //Marks this method as a bean factory. The return value (WebMvcConfigurer object) will be registered in the Spring container.
    public WebMvcConfigurer corsConfigurer() {
        //method that returns a WebMvcConfigurer implementation.
        //Just the name of this bean (same as method name).
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all endpoints
                        .allowedOrigins("http://localhost:4200") // React frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
