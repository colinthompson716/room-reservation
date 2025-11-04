package com.cothomp.room_reservation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

// creating a fake view resolver that tells spring whenever you try to 
// render a view, just output some mock html text instead
@TestConfiguration
public class TestViewResolverConfig {
    @Bean
    public ViewResolver testViewResolver() {
        return (viewName, locale) -> new View() {
            @Override
            public String getContentType() {
                return "text/html";
            }
            @Override
            public void render(java.util.Map<String, ?> model,
                               jakarta.servlet.http.HttpServletRequest request,
                               jakarta.servlet.http.HttpServletResponse response)
                    throws java.io.IOException {
                response.getWriter().write("<html><body>Mock View: " + viewName + "</body></html>");
            }
        };
    }
}
