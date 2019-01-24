package de.archilab.coalbase.learningoutcomeservice;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

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
public class CoalbaseLearningOutcomeApplicationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;


  @Test
  public void createLearningOutcome() throws Exception {

    Competence competence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.Synthese);

    Tool tool0 = new Tool(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren");
    Tool tool1 = new Tool(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen");
    Purpose purpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");

    LearningOutcome learningOutcomeToPost = new LearningOutcome(competence,
        Arrays.asList(tool0, tool1), purpose);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    mvc.perform(post("/learningOutcomes").content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(jsonPath("$.competence", is(objectMapper.writeValueAsString(competence))))
        .andExpect(jsonPath("$.tools[0]", is(objectMapper.writeValueAsString(tool0))))
        .andExpect(jsonPath("$.tools[1]", is(objectMapper.writeValueAsString(tool1))))
        .andExpect(jsonPath("$.purpose", is(objectMapper.writeValueAsString(purpose))))
        .andExpect(jsonPath("S._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) learningOutcomeRepository
        .findAll();
    assertFalse(learningOutcomes.isEmpty());

    LearningOutcome learningOutcome = learningOutcomes.get(0);
    assertNotNull(learningOutcome);
  }
}

