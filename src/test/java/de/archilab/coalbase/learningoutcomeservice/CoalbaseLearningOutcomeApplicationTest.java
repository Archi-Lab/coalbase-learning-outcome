package de.archilab.coalbase.learningoutcomeservice;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
  public void putLearningOutcome() throws Exception {
    LearningOutcome learningOutcomeToPut = buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPut);

    mvc.perform(put("/learningOutcomes/" + learningOutcomeToPut.getLearningOutcomeIdentifier())
        .content(json).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcomeToPut.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcomeToPut.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.tools[0].value", is(learningOutcomeToPut.getTools().get(0).getValue())))
        .andExpect(
            jsonPath("$.tools[1].value", is(learningOutcomeToPut.getTools().get(1).getValue())))
        .andExpect(jsonPath("$.purpose.value", is(learningOutcomeToPut.getPurpose().getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) learningOutcomeRepository
        .findAll();
    assertFalse(learningOutcomes.isEmpty());
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getLearningOutcomeIdentifier());
    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertEquals(savedLearningOutcome.getLearningOutcomeIdentifier(),
        learningOutcomeToPut.getLearningOutcomeIdentifier());
    assertEquals(savedLearningOutcome.getCompetence(), learningOutcomeToPut.getCompetence());
    assertEquals(savedLearningOutcome.getTools().get(0), learningOutcomeToPut.getTools().get(0));
    assertEquals(savedLearningOutcome.getTools().get(1), learningOutcomeToPut.getTools().get(1));
    assertEquals(savedLearningOutcome.getPurpose(), learningOutcomeToPut.getPurpose());
  }

  @Test
  public void patchLearningOutcome() throws Exception {
    LearningOutcomeIdentifier identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);

    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertNotNull(learningOutcome);

    Competence competence = new Competence("Action", TaxonomyLevel.ANALYSIS);
    Tool tool = new Tool("Tool");
    Purpose purpose = new Purpose("Purpose");
    LearningOutcome learningOutcomeToPatch = new LearningOutcome(identifier, competence,
        Arrays.asList(tool), purpose);

    ObjectMapper objectMapper = new ObjectMapper();
    String url = "/learningOutcomes/" + identifier.getId().toString();

    mvc.perform(patch(url).content(objectMapper.writeValueAsString(learningOutcomeToPatch)).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcomeToPatch.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcomeToPatch.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.tools[0].value", is(learningOutcomeToPatch.getTools().get(0).getValue())))
        .andExpect(jsonPath("$.purpose.value", is(learningOutcomeToPatch.getPurpose().getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

  }

  @Test
  public void deleteLearningOutcome() throws Exception {
    LearningOutcomeIdentifier identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);
    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertNotNull(learningOutcome);

    String url = "/learningOutcomes/" + identifier.getId().toString();
    mvc.perform(delete(url).with(csrf())).andExpect(status().isNoContent());

    Optional<LearningOutcome> optionalLearningOutcomeDeleted = this.learningOutcomeRepository
        .findById(identifier);
    assertFalse(optionalLearningOutcomeDeleted.isPresent());

  }

  @Test
  public void getLearningOutcomeByUUID() throws Exception {
    LearningOutcomeIdentifier identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);

    assertTrue(optionalLearningOutcome.isPresent());
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertNotNull(learningOutcome);

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

  @Test
  public void getLearningOutcomes() throws Exception {
    LearningOutcomeIdentifier identifier0 = this.createLearningOutcomeToRepo();
    LearningOutcomeIdentifier identifier1 = this.createLearningOutcomeToRepo();

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();

    String url = "/learningOutcomes/";

    mvc.perform(get(url).with(csrf())).andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].competence.action",
            is(learningOutcomes.get(0).getCompetence().getAction())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].competence.taxonomyLevel",
            is(learningOutcomes.get(0).getCompetence().getTaxonomyLevel().name())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].tools[0].value",
            is(learningOutcomes.get(0).getTools().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].tools[1].value",
            is(learningOutcomes.get(0).getTools().get(1).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].purpose.value",
            is(learningOutcomes.get(0).getPurpose().getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0]._links.self", notNullValue()))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].competence.action",
            is(learningOutcomes.get(1).getCompetence().getAction())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].competence.taxonomyLevel",
            is(learningOutcomes.get(1).getCompetence().getTaxonomyLevel().name())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].tools[0].value",
            is(learningOutcomes.get(1).getTools().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].tools[1].value",
            is(learningOutcomes.get(1).getTools().get(1).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].purpose.value",
            is(learningOutcomes.get(1).getPurpose().getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1]._links.self", notNullValue()));

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
