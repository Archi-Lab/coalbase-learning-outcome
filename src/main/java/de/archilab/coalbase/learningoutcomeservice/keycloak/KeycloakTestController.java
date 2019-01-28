package de.archilab.coalbase.learningoutcome.keycloak;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class KeycloakTestController {

  @RequestMapping(value="helloworld",method = RequestMethod.GET)
  public String getMyTestResource(){
    return "Hello World";
  }

  @RequestMapping(value="authorizedhelloworld",method = RequestMethod.GET)
  @PreAuthorize("hasRole('ROLE_COALBASE_USER')")
  public String getAuthorizedMyTestResource(){
    return "Hello World";
  }
}
