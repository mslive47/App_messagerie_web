package com.inf5190.chat;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.filter.AuthFilter;
import com.inf5190.chat.auth.session.SessionManager;
import com.inf5190.chat.messages.MessageController;

/**
 * Application spring boot.
 */
@SpringBootApplication
@PropertySource("classpath:cors.properties")
public class ChatApplication {

    @Value("${cors.allowedOrigins}")
    private String allowedOriginsConfig;

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    /**
     * Fonction qui enregistre le filtre d'authorization.
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authenticationFilter(SessionManager sessionManager) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(
                new AuthFilter(sessionManager, Arrays.asList(allowedOriginsConfig.split(","))));
        registrationBean.addUrlPatterns(MessageController.MESSAGES_PATH,
                AuthController.AUTH_LOGOUT_PATH);

        return registrationBean;
    }
}
