// src/main/java/com/imbус/knowledge/WebConfig.java
package com.imbus.knowledge;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Serve both uploads
        registry.addResourceHandler("/uploads/posts/**")
                .addResourceLocations("file:uploads/posts/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/uploads/content/**")
                .addResourceLocations("file:uploads/content/")
                .setCachePeriod(3600);
    }
}