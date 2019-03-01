package de.archilab.coalbase.learningoutcomeservice.learningspace;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
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
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Competence;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcomeRepository;
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

  @Autowired
  private LearningSpaceRepository learningSpaceRepository;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void createLearningSpace() throws Exception {
    LearningSpace learningSpaceToPost = this.buildSampleLearningSpaceWithRequirment();

    ObjectMapper objectMapper = new ObjectMapper();

    String learningSpaceAsJsonString = objectMapper.writeValueAsString(learningSpaceToPost);
    JSONObject jsonObject = new JSONObject(learningSpaceAsJsonString);
    jsonObject.put("learningOutcome",
        "learningOutcomes/" + learningSpaceToPost.getLearningOutcome()
            .getId());
    jsonObject.put("requirement",
        "learningSpaces/" + learningSpaceToPost.getRequirement()
            .getId());
    String learningSpaceAsJson = jsonObject.toString();

    this.mvc.perform(post("/learningSpaces")
        .with(csrf())
        .content(learningSpaceAsJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(201))
        .andExpect(
            jsonPath("$.title", is(learningSpaceToPost.getTitle())))
        .andExpect(
            jsonPath("$._links.learningOutcome", notNullValue()))
        .andExpect(
            jsonPath("$._links.requirement", notNullValue()))
        .andExpect(
            jsonPath("$._links.self", notNullValue())
        );
    List<LearningSpace> learningSpaceList = (List<LearningSpace>) this.learningSpaceRepository
        .findAll();
    assertEquals(learningSpaceList.size(), 2);
    Optional<LearningSpace> optionalLearningSpace = this.learningSpaceRepository
        .findById(learningSpaceList.get(1).getId());
    assertTrue(optionalLearningSpace.isPresent());
    LearningSpace learningSpace = optionalLearningSpace.get();
    assertEquals(learningSpace.getTitle(), learningSpaceToPost.getTitle());
    assertEquals(learningSpace.getLearningOutcome(), learningSpaceToPost.getLearningOutcome());
    assertEquals(learningSpace.getRequirement(), learningSpaceToPost.getRequirement());
  }

  private LearningSpace buildSampleLearningSpaceWithRequirment() {
    Competence competence = new Competence(
        "FirstCompetence",
        TaxonomyLevel.SYNTHESIS);

    Tool tool0 = new Tool(
        "FirstTool");
    Tool tool1 = new Tool(
        "SecondTool");
    Purpose purpose = new Purpose(
        "FirstPurpose");

    LearningOutcome firstLearningOutcome = new LearningOutcome(competence,
        Arrays.asList(tool0, tool1),
        purpose);
    this.learningOutcomeRepository.save(firstLearningOutcome);

    LearningSpace firstLearningSpace = new LearningSpace("FirstLearningSpace",
        firstLearningOutcome);
    this.learningSpaceRepository.save(firstLearningSpace);

    Competence secondCompetence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.SYNTHESIS);

    Tool secondTool0 = new Tool(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren");
    Tool secondTool1 = new Tool(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen");
    Purpose secondPurpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");

    LearningOutcome secondLearningOutcome = new LearningOutcome(secondCompetence,
        Arrays.asList(secondTool0, secondTool1),
        secondPurpose);
    this.learningOutcomeRepository.save(secondLearningOutcome);

    return new LearningSpace("LearningSpaceWithRequirement", secondLearningOutcome,
        firstLearningSpace);
  }
}
