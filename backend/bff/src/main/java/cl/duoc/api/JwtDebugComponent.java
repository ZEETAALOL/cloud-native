package cl.duoc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JwtDebugComponent implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JwtDebugComponent.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.audiences[0]}")
    private String expectedAudience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=".repeat(80));
        logger.info("JWT CONFIGURATION:");
        logger.info("Expected Issuer URI: {}", issuerUri);
        logger.info("Expected Audience: {}", expectedAudience);
        logger.info("=".repeat(80));
    }
}
