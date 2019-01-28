package de.archilab.coalbase.learningoutcomeservice;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
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

    LearningOutcome learningOutcomeToPost = buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    mvc.perform(post("/learningOutcomes").with(csrf()).content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcomeToPost.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcomeToPost.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.tools[0].value", is(learningOutcomeToPost.getTools().get(0).getValue())))
        .andExpect(
            jsonPath("$.tools[1].value", is(learningOutcomeToPost.getTools().get(1).getValue())))
        .andExpect(jsonPath("$.purpose.value", is(learningOutcomeToPost.getPurpose().getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) learningOutcomeRepository
        .findAll();
    assertFalse(learningOutcomes.isEmpty());
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getLearningOutcomeIdentifier());
    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertEquals(savedLearningOutcome.getCompetence(), learningOutcomeToPost.getCompetence());
    assertEquals(savedLearningOutcome.getTools().get(0), learningOutcomeToPost.getTools().get(0));
    assertEquals(savedLearningOutcome.getTools().get(1), learningOutcomeToPost.getTools().get(1));
    assertEquals(savedLearningOutcome.getPurpose(), learningOutcomeToPost.getPurpose());

  }

  @Test
  public void getLearningOutcomeByUUID() throws Exception {
    LearningOutcomeIdentifier identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);
    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome learningOutcome = optionalLearningOutcome.get();

    String url = "/learningOutcomes/" + identifier.getId().toString();

    mvc.perform(get(url).with(csrf())).andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.competence.action", is(learningOutcome.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcome.getCompetence().getTaxonomyLevel().name())))
        .andExpect(jsonPath("$.tools[0].value", is(learningOutcome.getTools().get(0).getValue())))
        .andExpect(jsonPath("$.tools[1].value", is(learningOutcome.getTools().get(1).getValue())))
        .andExpect(jsonPath("$.purpose.value", is(learningOutcome.getPurpose().getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

  }

  private LearningOutcomeIdentifier createLearningOutcomeToRepo() {
    final LearningOutcome learningOutcome = buildSampleLearningOutcome();
    this.learningOutcomeRepository.save(learningOutcome);
    return learningOutcome.getLearningOutcomeIdentifier();
  }

  private LearningOutcome buildSampleLearningOutcome() {
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

    return new LearningOutcome(learningOutcomeIdentifier,
        competence,
        Arrays.asList(tool0, tool1), purpose);

  }


}
