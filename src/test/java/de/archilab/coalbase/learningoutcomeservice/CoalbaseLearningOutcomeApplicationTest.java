package de.archilab.coalbase.learningoutcomeservice;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = BEFORE_CLASS)
@AutoConfigureMockMvc
@Transactional
public class CoalbaseLearningOutcomeApplicationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void notWhiteListedURLWithoutAuthenticationShouldFailWith401() throws Exception {
    this.mvc.perform(get("/helloworld").with(csrf())).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testAdmin", roles = {"admin"})
  public void notWhiteListedURLWithAdminRoleShouldSucceedWith200() throws Exception {
    this.mvc.perform(get("/helloworld").with(csrf())).andExpect(status().is(200));
  }

  @Test
  @WithMockUser(username = "testuser", roles = ("user"))
  public void getAuthorizedHelloWorldWithNotSufficientRolesShouldFail403() throws Exception {
    this.mvc.perform(get("/authorizedhelloworld").with(csrf())).andExpect(status().is(403));
  }
}
