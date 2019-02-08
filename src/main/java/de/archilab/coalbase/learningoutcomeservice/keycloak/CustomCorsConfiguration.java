package de.archilab.coalbase.learningoutcomeservice.keycloak;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CustomCorsConfiguration {

  private final String allowedOrigins;

  private final String allowedHeaders;

  public CustomCorsConfiguration(@Value("${security.cors.allowed-origins}") String allowedOrigins,
      @Value("${security.cors.allowed-headers}") String allowedHeaders) {
    this.allowedOrigins = allowedOrigins;
    this.allowedHeaders = allowedHeaders;
  }


  CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    for (final String origin : this.allowedOrigins.split(",")) {
      corsConfiguration.addAllowedOrigin(origin);
    }
    for (final String header : this.allowedHeaders.split(",")) {
      corsConfiguration.addAllowedHeader(header);
    }
    corsConfiguration.addAllowedOrigin("*");
    configurationSource.registerCorsConfiguration("/*", corsConfiguration);
    configurationSource.registerCorsConfiguration("/**", corsConfiguration);

    return configurationSource;
  }

}
