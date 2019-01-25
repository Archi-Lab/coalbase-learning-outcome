package de.archilab.coalbase.learningoutcomeservice;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Competence;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcomeIdentifier;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcomeRepository;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Purpose;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.TaxonomyLevel;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Tool;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CoalbaseLearningOutcomeApplicationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @Test
  public void createLearningOutcome() throws Exception {

    Competence competence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.SYNTHESIS);

    Tool tool0 = new Tool(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren");
    Tool tool1 = new Tool(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen");
    Purpose purpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");

    UUID uuid = UUID.randomUUID();
    LearningOutcomeIdentifier learningOutcomeIdentifier = new LearningOutcomeIdentifier(uuid);

    LearningOutcome learningOutcomeToPost = new LearningOutcome(learningOutcomeIdentifier,
        competence,
        Arrays.asList(tool0, tool1), purpose);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    mvc.perform(post("/learningOutcomes").content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(jsonPath("$.competence.action", is(competence.getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel", is(competence.getTaxonomyLevel().name())))
        .andExpect(jsonPath("$.tools[0].value", is(tool0.getValue())))
        .andExpect(jsonPath("$.tools[1].value", is(tool1.getValue())))
        .andExpect(jsonPath("$.purpose.value", is(purpose.getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) learningOutcomeRepository
        .findAll();
    assertFalse(learningOutcomes.isEmpty());
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository.findById(learningOutcomes.get(0).getLearningOutcomeIdentifier());
    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertEquals(savedLearningOutcome.getCompetence(), competence);
    assertEquals(savedLearningOutcome.getTools().get(0), tool0);
    assertEquals(savedLearningOutcome.getTools().get(1), tool1);
    assertEquals(savedLearningOutcome.getPurpose(), purpose);

  }
}

