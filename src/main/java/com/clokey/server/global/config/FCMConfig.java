package com.clokey.server.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {

    @Autowired
    private FirebaseProperties firebaseProperties;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        FirebaseApp firebaseApp = getFirebaseApp();
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private FirebaseApp getFirebaseApp() throws IOException {
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();

        if (!firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (FirebaseApp.DEFAULT_APP_NAME.equals(app.getName())) {
                    return app;
                }
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(getFirebaseConfigJson().getBytes())))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    private String getFirebaseConfigJson() {
        return String.format(
                "{ \"type\": \"%s\", \"project_id\": \"%s\", \"private_key_id\": \"%s\", \"private_key\": \"%s\", \"client_email\": \"%s\", \"client_id\": \"%s\", \"auth_uri\": \"%s\", \"token_uri\": \"%s\", \"auth_provider_x509_cert_url\": \"%s\", \"client_x509_cert_url\": \"%s\" }",
                firebaseProperties.getType(),
                firebaseProperties.getProjectId(),
                firebaseProperties.getPrivateKeyId(),
                firebaseProperties.getPrivateKey().replace("\\n", "\n"),  // 줄바꿈 처리
                firebaseProperties.getClientEmail(),
                firebaseProperties.getClientId(),
                firebaseProperties.getAuthUri(),
                firebaseProperties.getTokenUri(),
                firebaseProperties.getAuthProviderX509CertUrl(),
                firebaseProperties.getClientX509CertUrl()
        );
    }
}
