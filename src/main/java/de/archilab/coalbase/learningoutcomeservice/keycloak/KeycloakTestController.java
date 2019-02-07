package de.archilab.coalbase.learningoutcomeservice.keycloak;

import java.security.Principal;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin("*")
public class KeycloakTestController {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(KeycloakTestController.class);

  @RequestMapping(value = "helloworld", method = RequestMethod.GET)
  public String getMyTestResource() {
    return "Hello World";
  }

  @RequestMapping(value = "authorizedhelloworld", method = RequestMethod.GET)
  public String getAuthorizedMyTestResource(Principal principal) {
    return "Authorized Hello " + principal.getName();
  }
}
