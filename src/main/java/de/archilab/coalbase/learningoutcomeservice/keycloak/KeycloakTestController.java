package de.archilab.coalbase.learningoutcomeservice.keycloak;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.security.Principal;
import java.util.Set;

import javax.annotation.security.PermitAll;


@RestController
@CrossOrigin("*")
public class KeycloakTestController {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KeycloakTestController.class);

  @RequestMapping(value="helloworld",method = RequestMethod.GET)
  public String getMyTestResource(){
    return "Hello World";
  }

  @RequestMapping(value="authorizedhelloworld",method = RequestMethod.GET)
  @PreAuthorize("hasRole('coalbase_user')")
  public String getAuthorizedMyTestResource(Principal principal){
    return "Authorized Hello " + principal.getName();
  }
}
