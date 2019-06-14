package de.archilab.coalbase.learningoutcomeservice.learningspace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.archilab.coalbase.learningoutcomeservice.learningoutcome.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.Ability;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = BEFORE_CLASS)
@AutoConfigureMockMvc
@Transactional
public class AggregateLearningSpaceTest {

  private static final String TOPIC = "learning-space";

  @ClassRule
  public static final EmbeddedKafkaRule BROKER = new EmbeddedKafkaRule(1, false,
      AggregateLearningSpaceTest.TOPIC);

  private static BlockingQueue<ConsumerRecord<String, String>> records;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningSpaceRepository learningSpaceRepository;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @BeforeClass
  public static void setupKafka() {
    System.setProperty("spring.kafka.bootstrap-servers",
        AggregateLearningSpaceTest.BROKER.getEmbeddedKafka().getBrokersAsString());

    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("testT", "false",
            AggregateLearningSpaceTest.BROKER.getEmbeddedKafka());

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(
        AggregateLearningSpaceTest.TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    AggregateLearningSpaceTest.records = new LinkedBlockingQueue<>();
    container.setupMessageListener(
        (MessageListener<String, String>) record -> AggregateLearningSpaceTest.records
            .add(record));

    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils
        .waitForAssignment(container,
            AggregateLearningSpaceTest.BROKER.getEmbeddedKafka()
                .getPartitionsPerTopic());

  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
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


    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent learningSpaceDomainEvent = objectMapper
        .readValue(record.value(), LearningSpaceDomainEvent.class);
    assertThat(learningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.CREATED.name());
    assertThat(learningSpaceDomainEvent.getLearningSpaceIdentifier())
        .isEqualTo(learningSpace.getId());
  }

  @Test
  public void canNotPostLearningSpaceWithoutAuthentication() throws Exception {
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

    this.mvc.perform(post("/learningSpaces").with(csrf()).content(learningSpaceAsJson)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void putLearningSpace() throws Exception {
    LearningSpace learningSpaceToPut = this.buildSampleLearningSpaceWithRequirment();

    ObjectMapper objectMapper = new ObjectMapper();

    String learningSpaceAsJsonString = objectMapper.writeValueAsString(learningSpaceToPut);
    JSONObject jsonObject = new JSONObject(learningSpaceAsJsonString);
    jsonObject.put("learningOutcome",
        "learningOutcomes/" + learningSpaceToPut.getLearningOutcome()
            .getId());
    jsonObject.put("requirement",
        "learningSpaces/" + learningSpaceToPut.getRequirement()
            .getId());
    String learningSpaceAsJson = jsonObject.toString();

    this.mvc.perform(put("/learningSpaces/" + learningSpaceToPut.getId().toString())
        .content(learningSpaceAsJson).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(
            jsonPath("$.title", is(learningSpaceToPut.getTitle())))
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
    assertEquals(learningSpace.getTitle(), learningSpaceToPut.getTitle());
    assertEquals(learningSpace.getLearningOutcome(), learningSpaceToPut.getLearningOutcome());
    assertEquals(learningSpace.getRequirement(), learningSpaceToPut.getRequirement());

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent learningSpaceDomainEvent = objectMapper
        .readValue(record.value(), LearningSpaceDomainEvent.class);
    assertThat(learningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.CREATED.name());
    assertThat(learningSpaceDomainEvent.getLearningSpaceIdentifier())
        .isEqualTo(learningSpace.getId());
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void expectUpdateEventOnSecondPutLearningSpace() throws Exception {
    LearningSpace learningSpaceToPut = this.buildSampleLearningSpaceWithRequirment();

    ObjectMapper objectMapper = new ObjectMapper();

    String learningSpaceAsJsonString = objectMapper.writeValueAsString(learningSpaceToPut);
    JSONObject jsonObject = new JSONObject(learningSpaceAsJsonString);
    jsonObject.put("learningOutcome",
        "learningOutcomes/" + learningSpaceToPut.getLearningOutcome()
            .getId());
    jsonObject.put("requirement",
        "learningSpaces/" + learningSpaceToPut.getRequirement()
            .getId());
    String learningSpaceAsJson = jsonObject.toString();

    this.mvc.perform(put("/learningSpaces/" + learningSpaceToPut.getId().toString())
        .content(learningSpaceAsJson).with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent learningSpaceDomainEvent = objectMapper
        .readValue(record.value(), LearningSpaceDomainEvent.class);
    assertThat(learningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.CREATED.name());

    this.mvc.perform(put("/learningSpaces/" + learningSpaceToPut.getId().toString())
        .content(learningSpaceAsJson).with(csrf())
        .contentType(MediaType.APPLICATION_JSON));

    List<LearningSpace> learningSpaceList = (List<LearningSpace>) this.learningSpaceRepository
        .findAll();
    assertEquals(learningSpaceList.size(), 2);
    Optional<LearningSpace> optionalLearningSpace = this.learningSpaceRepository
        .findById(learningSpaceList.get(1).getId());
    assertTrue(optionalLearningSpace.isPresent());
    LearningSpace learningSpace = optionalLearningSpace.get();
    assertEquals(learningSpace.getTitle(), learningSpaceToPut.getTitle());
    assertEquals(learningSpace.getLearningOutcome(), learningSpaceToPut.getLearningOutcome());
    assertEquals(learningSpace.getRequirement(), learningSpaceToPut.getRequirement());

    /*Test kafka message */
    ConsumerRecord<String, String> secondRecord = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent secondLearningSpaceDomainEvent = objectMapper
        .readValue(secondRecord.value(), LearningSpaceDomainEvent.class);
    assertThat(secondLearningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.UPDATED.name());
    assertThat(secondLearningSpaceDomainEvent.getLearningSpaceIdentifier())
        .isEqualTo(learningSpace.getId());
  }

  @Test
  public void canNotPutLearningSpaceWithoutAuthentication() throws Exception {
    LearningSpace learningSpaceToPut = this.buildSampleLearningSpaceWithRequirment();

    ObjectMapper objectMapper = new ObjectMapper();

    String learningSpaceAsJsonString = objectMapper.writeValueAsString(learningSpaceToPut);
    JSONObject jsonObject = new JSONObject(learningSpaceAsJsonString);
    jsonObject.put("learningOutcome",
        "learningOutcomes/" + learningSpaceToPut.getLearningOutcome()
            .getId());
    jsonObject.put("requirement",
        "learningSpaces/" + learningSpaceToPut.getRequirement()
            .getId());
    String learningSpaceAsJson = jsonObject.toString();

    this.mvc.perform(put("/learningSpaces/" + learningSpaceToPut.getId().toString())
        .content(learningSpaceAsJson).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void patchLearningSpace() throws Exception {
    UniqueId<LearningSpace> identifier = this.buildAndSaveLearningSpaceWithRequirment();
    Optional<LearningSpace> optionalLearningSpace = this.learningSpaceRepository
        .findById(identifier);

    LearningSpace learningSpace = optionalLearningSpace.orElse(null);
    assertThat(learningSpace).isNotNull();

    learningSpace.setTitle("NewTitleAfterPatch");

    ObjectMapper objectMapper = new ObjectMapper();

    String learningSpaceAsJsonString = objectMapper.writeValueAsString(learningSpace);
    JSONObject jsonObject = new JSONObject(learningSpaceAsJsonString);
    jsonObject.put("learningOutcome",
        "learningOutcomes/" + learningSpace.getLearningOutcome()
            .getId());
    jsonObject.put("requirement",
        "learningSpaces/" + learningSpace.getRequirement()
            .getId());
    String learningSpaceAsJson = jsonObject.toString();

    this.mvc.perform(patch("/learningSpaces/" + learningSpace.getId().toString())
        .content(learningSpaceAsJson).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200))
        .andExpect(
            jsonPath("$.title", is(learningSpace.getTitle())))
        .andExpect(
            jsonPath("$._links.learningOutcome", notNullValue()))
        .andExpect(
            jsonPath("$._links.requirement", notNullValue()))
        .andExpect(
            jsonPath("$._links.self", notNullValue())
        );

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent learningSpaceDomainEvent = objectMapper
        .readValue(record.value(), LearningSpaceDomainEvent.class);
    assertThat(learningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.UPDATED.name());
    assertThat(learningSpaceDomainEvent.getLearningSpaceIdentifier())
        .isEqualTo(learningSpace.getId());
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void deleteLearningSpace() throws Exception {
    UniqueId<LearningSpace> identifier = this.buildAndSaveLearningSpaceWithRequirment();
    Optional<LearningSpace> optionalLearningSpace = this.learningSpaceRepository
        .findById(identifier);

    LearningSpace learningSpace = optionalLearningSpace.orElse(null);
    assertThat(learningSpace).isNotNull();

    this.mvc.perform(delete("/learningSpaces/" + learningSpace.getId().toString()).with(csrf()))
        .andExpect(status().isNoContent());

    Optional<LearningSpace> optionalLearningSpaceDeleted = this.learningSpaceRepository
        .findById(identifier);
    assertThat(optionalLearningSpaceDeleted.isPresent()).isFalse();

    ConsumerRecord<String, String> record = AggregateLearningSpaceTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningSpaceDomainEvent learningSpaceDomainEvent = new ObjectMapper()
        .readValue(record.value(), LearningSpaceDomainEvent.class);
    assertThat(learningSpaceDomainEvent.getEventType())
        .isEqualTo(LearningSpaceEventType.DELETED.name());
    assertThat(learningSpaceDomainEvent.getLearningSpaceIdentifier())
        .isEqualTo(learningSpace.getId());
  }

  @Test
  public void canNotDeleteLearningSpaceWithoutAuthentication() throws Exception {
    this.mvc.perform(delete("learningSpaces/1").with(csrf())).andExpect(status().is(401));
  }

  @Test
  public void getLearningSpaceByID() throws Exception {
    UniqueId<LearningSpace> identifier = this.buildAndSaveLearningSpaceWithRequirment();
    Optional<LearningSpace> optionalLearningSpace = this.learningSpaceRepository
        .findById(identifier);

    LearningSpace learningSpace = optionalLearningSpace.orElse(null);
    assertThat(learningSpace).isNotNull();

    this.mvc.perform(get("/learningSpaces/" + learningSpace.getId().toString()).with(csrf()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(status().is(200))
        .andExpect(
            jsonPath("$.title", is(learningSpace.getTitle())))
        .andExpect(
            jsonPath("$._links.learningOutcome", notNullValue()))
        .andExpect(
            jsonPath("$._links.requirement", notNullValue()))
        .andExpect(
            jsonPath("$._links.self", notNullValue())
        );
  }

  @Test
  public void getLearningSpaces() throws Exception {
    this.buildAndSaveLearningSpaceWithRequirment();

    List<LearningSpace> learningSpaces = (List<LearningSpace>) this.learningSpaceRepository
        .findAll();

    this.mvc.perform(get("/learningSpaces").with(csrf()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(status().is(200))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[0].title", is(learningSpaces.get(0).getTitle())))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[0]._links.learningOutcome", notNullValue()))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[0]._links.requirement", notNullValue()))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[0]._links.self", notNullValue())
        )
        .andExpect(
            jsonPath("$._embedded.learningSpaces[1].title", is(learningSpaces.get(1).getTitle())))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[1]._links.learningOutcome", notNullValue()))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[1]._links.requirement", notNullValue()))
        .andExpect(
            jsonPath("$._embedded.learningSpaces[1]._links.self", notNullValue())
        );

  }


  private UniqueId<LearningSpace> buildAndSaveLearningSpaceWithRequirment() {
    final LearningSpace learningSpace = this.buildSampleLearningSpaceWithRequirment();
    this.learningSpaceRepository.save(learningSpace);
    return learningSpace.getId();
  }

  private LearningSpace buildSampleLearningSpaceWithRequirment() {
    Role role = new Role("Student");

    Competence competence = new Competence(
        "FirstCompetence",
        TaxonomyLevel.SYNTHESIS);

    Requirement requirement = new Requirement("Alles können", TaxonomyLevel.EVALUATION);

    Ability ability0 = new Ability(
        "FirstTool", TaxonomyLevel.EVALUATION);
    Ability ability1 = new Ability(
        "SecondTool", TaxonomyLevel.ANALYSIS);
    Purpose purpose = new Purpose(
        "FirstPurpose");

    LearningOutcome firstLearningOutcome = new LearningOutcome(role, competence,
        Arrays.asList(requirement),
        Arrays.asList(ability0, ability1),
        Arrays.asList(purpose));
    this.learningOutcomeRepository.save(firstLearningOutcome);

    LearningSpace firstLearningSpace = new LearningSpace("FirstLearningSpace",
        firstLearningOutcome);
    this.learningSpaceRepository.save(firstLearningSpace);

    Role secondRole = new Role("Student");

    Competence secondCompetence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.SYNTHESIS);

    Requirement secondRequirement = new Requirement("Alles können und noch mehr", TaxonomyLevel.EVALUATION);

    Ability secondAbility0 = new Ability(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren", TaxonomyLevel.EVALUATION);
    Ability secondAbility1 = new Ability(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen", TaxonomyLevel.EVALUATION);
    Purpose secondPurpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");

    LearningOutcome secondLearningOutcome = new LearningOutcome(secondRole,
        secondCompetence,
        Arrays.asList(secondRequirement),
        Arrays.asList(secondAbility0, secondAbility1),
        Arrays.asList(secondPurpose));
    this.learningOutcomeRepository.save(secondLearningOutcome);

    return new LearningSpace("LearningSpaceWithRequirement", secondLearningOutcome,
        firstLearningSpace);
  }
}
