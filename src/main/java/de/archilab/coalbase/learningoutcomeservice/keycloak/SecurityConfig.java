package de.archilab.coalbase.learningoutcomeservice.keycloak;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

//@KeycloakConfiguration
//Following is just a workaround due to a bug in the keycloak adapter (in connection with spring boot 2), see:
//https://issues.jboss.org/browse/KEYCLOAK-8725
@Configuration
@ComponentScan(
    basePackageClasses = KeycloakSecurityComponents.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "org.keycloak.adapters.springsecurity.management.HttpSessionManager"
    )
)
//end of workaround
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  private static final String ROLE_STUDENT = "coalbase_student";
  private static final String ROLE_PROFESSOR = "coalbase_professor";
  private static final String ROLE_ADMIN = "coalbase_admin";
  private static final String H2_CONSOLE = "/h2-console";
  private static final String H2_CONSOLE_SUB = "/h2-console/*";

  @Autowired
  private CustomCorsConfiguration customCorsConfiguration;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider
        = this.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    // Since this is a stateless REST service, no sessions are needed
    return new NullAuthenticatedSessionStrategy();
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    super.configure(httpSecurity);
    this.generalHttpSecuritySettings(httpSecurity);
    this.whitelistHttpSecuritySettings(httpSecurity);
    this.restrictAllChangesToProfessor("learningOutcomes", httpSecurity);
    this.restrictAllChangesToProfessor("learningSpaces", httpSecurity);
    this.restrictAllChangesToProfessor("semesters", httpSecurity);
    this.restrictAllChangesToProfessor("courses", httpSecurity);
    this.fallbackHttpSecuritySettings(httpSecurity);
  }

  private void generalHttpSecuritySettings(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .cors().configurationSource(this.customCorsConfiguration.corsConfigurationSource())
        .and()

        // Possible security issue! Take a look into this!
        .csrf().disable()
        // Needed for H2 console. Possible security issue!
        .headers().frameOptions().sameOrigin()

        .and()
        .authorizeRequests();


  }

  private void whitelistHttpSecuritySettings(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeRequests()
        //WhiteList local
        // H2-Console
        .antMatchers(SecurityConfig.H2_CONSOLE, SecurityConfig.H2_CONSOLE_SUB)
        .permitAll()

        //WhiteList prod
        .antMatchers("/browser/**")
        .permitAll()

        .antMatchers("/profile/**")
        .permitAll()

        .antMatchers("/studyRooms")
        .permitAll()

        .antMatchers("/studyRooms/**")
        .permitAll();
  }

  private void fallbackHttpSecuritySettings(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeRequests()
        .anyRequest().hasRole(SecurityConfig.ROLE_ADMIN);
  }

  private void restrictAllChangesToProfessor(String resource, HttpSecurity httpSecurity)
      throws Exception {
    String listResource = "/" + resource;
    String itemResource = listResource + "/*";
    String associationResource = itemResource + "/**";
    httpSecurity
        .authorizeRequests()
        //LearningOutcome (Standard endpoints provided by SDR)

        //ListResource
        .antMatchers(HttpMethod.OPTIONS, listResource)
        .permitAll()

        .antMatchers(HttpMethod.GET, listResource)
        .permitAll()

        .antMatchers(HttpMethod.HEAD, listResource)
        .permitAll()

        .antMatchers(HttpMethod.POST, listResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)
        //ItemResource
        .antMatchers(HttpMethod.GET, itemResource)
        .permitAll()

        .antMatchers(HttpMethod.HEAD, itemResource)
        .permitAll()

        .antMatchers(HttpMethod.PUT, itemResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)

        .antMatchers(HttpMethod.PATCH, itemResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)

        .antMatchers(HttpMethod.DELETE, itemResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)
        //AssociationResource
        .antMatchers(HttpMethod.GET, associationResource)
        .permitAll()

        .antMatchers(HttpMethod.PUT, associationResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)

        .antMatchers(HttpMethod.PATCH, associationResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)

        .antMatchers(HttpMethod.POST, associationResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN)

        .antMatchers(HttpMethod.DELETE, associationResource)
        .hasAnyRole(SecurityConfig.ROLE_PROFESSOR, SecurityConfig.ROLE_ADMIN);
  }
}
