package com.inf5190.chat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.google.firebase.FirebaseApp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application spring boot.
 */
@SpringBootApplication
@PropertySource("classpath:cors.properties")
public class ChatApplication {

    @Value("${cors.allowedOrigins}")
    private String allowedOriginsConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatApplication.class);
    private static final  String BUCKET_NAME = "inf5190-chat-faee1.firebasestorage.app";

    public static void main(String[] args) {
        //SpringApplication.run(ChatApplication.class, args);
       // SessionManager sessionManager = new SessionManager();
        //String key = sessionManager.generateSecretKey();
        //System.out.println("la clé est : " + key);
        try {
            if (FirebaseApp.getApps().size() == 0) {
                FileInputStream serviceAccount = new
                        FileInputStream("firebase-key.json");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(BUCKET_NAME)
                        .build();
                LOGGER.info("Initializing Firebase application.");
                FirebaseApp.initializeApp(options);
            }
            LOGGER.info("Firebase application already initialized.");
            SpringApplication.run(ChatApplication.class, args);
        } catch (IOException e) {
            System.err.println("Could not initialise application. Please check you service account key path");
        }
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

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
