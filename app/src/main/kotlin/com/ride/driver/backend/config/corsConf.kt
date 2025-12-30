package com.ride.driver.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

	override fun addCorsMappings(registry: CorsRegistry) {

		registry.addMapping("/api/**")
				.allowedOrigins("http://localhost:5173")
				.allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS")
				// .allowedHeaders("header1", "header2", "header3")
				// .exposedHeaders("header1", "header2")
				// .allowCredentials(true).maxAge(3600)

		// Add more mappings...
	}
}