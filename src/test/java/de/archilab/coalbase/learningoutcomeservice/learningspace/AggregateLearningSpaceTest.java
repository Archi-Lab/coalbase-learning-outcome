package de.archilab.coalbase.learningoutcomeservice.learningspace;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Competence;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Purpose;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.TaxonomyLevel;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Tool;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AggregateLearningSpaceTest {

  @Autowired
  private MockMvc mvc;


  @BeforeClass
  public static void createInitalLearningSpace() {
    // TODO create a LearningSpace, that can be referenced

  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void createLearningSpace() throws Exception {
    LearningSpace learningSpaceToPost = this.buildSampleLearningSpace();

    ObjectMapper objectMapper = new ObjectMapper();
    String learningSpaceAsJson = objectMapper.writeValueAsString(learningSpaceToPost);

    this.mvc.perform(post("/learningSpaces")
        .with(csrf())
        .content(learningSpaceAsJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(201));

  }

  private LearningSpace buildSampleLearningSpace() {
    Competence competence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.SYNTHESIS);

    Tool tool0 = new Tool(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren");
    Tool tool1 = new Tool(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen");
    Purpose purpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");

    LearningOutcome learningOutcome = new LearningOutcome(competence, Arrays.asList(tool0, tool1),
        purpose);
    return new LearningSpace("LearningSpaceTitle", learningOutcome);
  }
}
