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
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.keycloak.adapters.springsecurity.management.HttpSessionManager"))
//end of workaround
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth){
    KeycloakAuthenticationProvider keycloakAuthenticationProvider
        = keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver(){
    return new KeycloakSpringBootConfigResolver();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    //Since this is a stateless REST service, no sessions are needed
    return new NullAuthenticatedSessionStrategy();
  }

  private static final String ROLE_STUDENT = "coalbase_student";
  private static final String ROLE_PROFESSOR = "coalbase_professor";
  private static final String ROLE_ADMIN = "coalbase_admin";

  private static final String LO_LIST_RESOURCE = "/learningOutcomes";
  private static final String LO_ITEM_RESOURCE = "/learningOutcomes/*";
  private static final String LO_ASSOCIATION_RESOURCE = "/learningOutcomes/*/**";
  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception{
    super.configure(httpSecurity);
    httpSecurity.cors()
        .and()
        .csrf().disable() //Possible Security Issue! Take a look into this!
        .authorizeRequests()
        .antMatchers("/browser/**").permitAll()
        .antMatchers("/profile/**").permitAll()
        .antMatchers("/studyRooms").permitAll()
        .antMatchers("/studyRooms/**").permitAll()
        //LearningOutcome (Standard endpoints provided by SDR)
        //ListResource
        .antMatchers(HttpMethod.GET, LO_LIST_RESOURCE).permitAll()
        .antMatchers(HttpMethod.HEAD, LO_LIST_RESOURCE).permitAll()
        .antMatchers(HttpMethod.POST, LO_LIST_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        //ItemResource
        .antMatchers(HttpMethod.GET, LO_ITEM_RESOURCE).permitAll()
        .antMatchers(HttpMethod.HEAD, LO_ITEM_RESOURCE).permitAll()
        .antMatchers(HttpMethod.PUT, LO_ITEM_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        .antMatchers(HttpMethod.PATCH, LO_ITEM_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        .antMatchers(HttpMethod.DELETE, LO_ITEM_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        //AssociationResource
        .antMatchers(HttpMethod.GET, LO_ASSOCIATION_RESOURCE).permitAll()
        .antMatchers(HttpMethod.PUT, LO_ASSOCIATION_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        .antMatchers(HttpMethod.POST, LO_ASSOCIATION_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)
        .antMatchers(HttpMethod.DELETE, LO_ASSOCIATION_RESOURCE).hasAnyRole(ROLE_PROFESSOR, ROLE_ADMIN)

        .and()
        .authorizeRequests()
        .anyRequest().hasRole(ROLE_ADMIN);
  }
}
