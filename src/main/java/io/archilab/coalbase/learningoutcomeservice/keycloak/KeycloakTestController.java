package io.archilab.coalbase.learningoutcomeservice.keycloak;

import java.security.Principal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin("*")
public class KeycloakTestController {

  @RequestMapping(value = "helloworld", method = RequestMethod.GET)
  public String getMyTestResource() {
    return "Hello World";
  }

  @RequestMapping(value = "authorizedhelloworld", method = RequestMethod.GET)
  public String getAuthorizedMyTestResource(Principal principal) {
    return "Authorized Hello " + principal.getName();
  }
}
