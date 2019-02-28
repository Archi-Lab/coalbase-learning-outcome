package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {
  private static final String TOPIC = "learning-outcome";

  @ClassRule
  public final static EmbeddedKafkaRule BROKER = new EmbeddedKafkaRule(1,
      false, IntegrationTest.TOPIC);

  private static BlockingQueue<ConsumerRecord<String, String>> records;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @BeforeClass
  public static void setup() {
    System.setProperty("spring.kafka.bootstrap-servers",
        IntegrationTest.BROKER.getEmbeddedKafka().getBrokersAsString());

    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("testT", "false",
            IntegrationTest.BROKER.getEmbeddedKafka());

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(
        IntegrationTest.TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    IntegrationTest.records = new LinkedBlockingQueue<>();
    container.setupMessageListener(
        (MessageListener<String, String>) record -> IntegrationTest.records
            .add(record));

    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils
        .waitForAssignment(container,
            IntegrationTest.BROKER.getEmbeddedKafka()
                .getPartitionsPerTopic());

  }

  @Test
  public void notWhiteListedURLWithoutAuthenticationShouldFailWith401() throws Exception {
    this.mvc.perform(get("/helloworld").with(csrf())).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testAdmin", roles = {"coalbase_admin"})
  public void notWhiteListedURLWithAdminRoleShouldSucceedWith200() throws Exception {
    this.mvc.perform(get("/helloworld").with(csrf())).andExpect(status().is(200));
  }

  @Test
  @WithMockUser(username = "testuser", roles = ("coalbase_user"))
  public void getAuthorizedHelloWorldWithNotSufficientRolesShouldFail403() throws Exception {
    this.mvc.perform(get("/authorizedhelloworld").with(csrf())).andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void createLearningOutcome() throws Exception {

    LearningOutcome learningOutcomeToPost = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    this.mvc.perform(post("/learningOutcomes").with(csrf()).content(json)
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

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();
    assertThat(learningOutcomes.isEmpty()).isFalse();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getId());
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertThat(savedLearningOutcome.getCompetence())
        .isEqualTo(learningOutcomeToPost.getCompetence());
    assertThat(savedLearningOutcome.getTools().get(1))
        .isEqualTo(learningOutcomeToPost.getTools().get(1));
    assertThat(savedLearningOutcome.getTools().get(0))
        .isEqualTo(learningOutcomeToPost.getTools().get(0));
    assertThat(savedLearningOutcome.getPurpose()).isEqualTo(learningOutcomeToPost.getPurpose());

    /*Test kafka message */
    ConsumerRecord<String, String> record = IntegrationTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningOutcomeDomainEvent learningOutcomeDomainEvent = objectMapper
        .readValue(record.value(), LearningOutcomeDomainEvent.class);
    assertThat(learningOutcomeDomainEvent.getEventType())
        .isEqualTo(LearningOutcomeEventType.CREATED.name());
    assertThat(learningOutcomeDomainEvent.getLearningOutcomeIdentifier())
        .isEqualTo(learningOutcomes.get(0).getId());
  }

  @Test
  public void createLearningOutcomeWithoutAuthenticationShouldFail() throws Exception {

    LearningOutcome learningOutcomeToPost = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    this.mvc.perform(post("/learningOutcomes").with(csrf()).content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void putLearningOutcome() throws Exception {
    LearningOutcome learningOutcomeToPut = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPut);

    this.mvc.perform(put("/learningOutcomes/" + learningOutcomeToPut.getId().toString())
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

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();
    assertThat(learningOutcomes.isEmpty()).isFalse();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getId());
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertThat(savedLearningOutcome.getId()).isEqualTo(learningOutcomeToPut.getId());
    assertThat(savedLearningOutcome.getCompetence())
        .isEqualTo(learningOutcomeToPut.getCompetence());
    assertThat(savedLearningOutcome.getTools().get(0))
        .isEqualTo(learningOutcomeToPut.getTools().get(0));
    assertThat(savedLearningOutcome.getTools().get(1))
        .isEqualTo(learningOutcomeToPut.getTools().get(1));
    assertThat(savedLearningOutcome.getPurpose()).isEqualTo(learningOutcomeToPut.getPurpose());

    /*Test kafka message */
    ConsumerRecord<String, String> record = IntegrationTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningOutcomeDomainEvent learningOutcomeDomainEvent = objectMapper
        .readValue(record.value(), LearningOutcomeDomainEvent.class);
    assertThat(learningOutcomeDomainEvent.getEventType())
        .isEqualTo(LearningOutcomeEventType.CREATED.name());
    assertThat(learningOutcomeDomainEvent.getLearningOutcomeIdentifier())
        .isEqualTo(savedLearningOutcome.getId());
  }

  @Test
  public void putLearningOutcomeWithoutAuthenticationShouldFail() throws Exception {
    LearningOutcome learningOutcomeToPut = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPut);

    this.mvc.perform(put("/learningOutcomes/" + learningOutcomeToPut.getId().toString())
        .content(json).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void patchLearningOutcome() throws Exception {
    UniqueId<LearningOutcome> identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);

    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertThat(learningOutcome).isNotNull();

    Competence competence = new Competence("Action", TaxonomyLevel.ANALYSIS);
    learningOutcome.setCompetence(competence);

    Tool tool = new Tool("Tool");
    learningOutcome.addTool(tool);

    Purpose purpose = new Purpose("Purpose");
    learningOutcome.setPurpose(purpose);

    ObjectMapper objectMapper = new ObjectMapper();
    String url = "/learningOutcomes/" + identifier.toString();

    this.mvc.perform(
        patch(url).content(objectMapper.writeValueAsString(learningOutcome)).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcome.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcome.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.tools[0].value", is(learningOutcome.getTools().get(0).getValue())))
        .andExpect(
            jsonPath("$.tools[1].value", is(learningOutcome.getTools().get(1).getValue())))
        .andExpect(
            jsonPath("$.tools[2].value", is(learningOutcome.getTools().get(2).getValue())))
        .andExpect(jsonPath("$.purpose.value", is(learningOutcome.getPurpose().getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    /*Test kafka message */
    ConsumerRecord<String, String> record = IntegrationTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningOutcomeDomainEvent learningOutcomeDomainEvent = objectMapper
        .readValue(record.value(), LearningOutcomeDomainEvent.class);
    assertThat(learningOutcomeDomainEvent.getEventType())
        .isEqualTo(LearningOutcomeEventType.UPDATED.name());
    assertThat(learningOutcomeDomainEvent.getLearningOutcomeIdentifier())
        .isEqualTo(learningOutcome.getId());
  }

/*  @Test
  //THIS IS A WORKAROUND FOR NOW! THE TEST SHOULD NEED THIS AUTHORIZATION, IT SHOULDNT BE NECESSARY TO SAVE A LO TO THE REPO FIRST!
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void patchLearningOutcomeWithoutAuthorizationShouldFail() throws Exception {
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
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }*/

  @Test
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void deleteLearningOutcome() throws Exception {
    UniqueId<LearningOutcome> identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertThat(learningOutcome).isNotNull();

    String url = "/learningOutcomes/" + identifier.toString();
    this.mvc.perform(delete(url).with(csrf())).andExpect(status().isNoContent());

    Optional<LearningOutcome> optionalLearningOutcomeDeleted = this.learningOutcomeRepository
        .findById(identifier);
    assertThat(optionalLearningOutcomeDeleted.isPresent()).isFalse();

    /*Test kafka message */
    ConsumerRecord<String, String> record = IntegrationTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningOutcomeDomainEvent learningOutcomeDomainEvent = new ObjectMapper()
        .readValue(record.value(), LearningOutcomeDomainEvent.class);
    assertThat(learningOutcomeDomainEvent.getEventType())
        .isEqualTo(LearningOutcomeEventType.DELETED.name());
    assertThat(learningOutcomeDomainEvent.getLearningOutcomeIdentifier())
        .isEqualTo(learningOutcome.getId());
  }

  @Test
  public void deleteLearningOutcomeWithoutAuthenticationShouldFail() throws Exception {
    this.mvc.perform(delete("learningOutcomes/1").with(csrf())).andExpect(status().is(401));
  }

  @Test
  //THIS IS A WORKAROUND FOR NOW! THE TEST SHOULD NEED THIS AUTHORIZATION, IT SHOULDNT BE NECESSARY TO SAVE A LO TO THE REPO FIRST!
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void getLearningOutcomeByUUID() throws Exception {
    UniqueId<LearningOutcome> identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertThat(learningOutcome).isNotNull();

    String url = "/learningOutcomes/" + identifier.toString();

    this.mvc.perform(get(url).with(csrf())).andExpect(status().isOk())
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
  //THIS IS A WORKAROUND FOR NOW! THE TEST SHOULD NEED THIS AUTHORIZATION, IT SHOULDNT BE NECESSARY TO SAVE A LO TO THE REPO FIRST!
  @WithMockUser(username = "testProfessor", roles = {"coalbase_professor"})
  public void getLearningOutcomes() throws Exception {
    this.createLearningOutcomeToRepo();
    this.createLearningOutcomeToRepo();

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();

    String url = "/learningOutcomes/";

    this.mvc.perform(get(url).with(csrf())).andExpect(status().isOk())
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

  private UniqueId<LearningOutcome> createLearningOutcomeToRepo() {
    final LearningOutcome learningOutcome = this.buildSampleLearningOutcome();
    this.learningOutcomeRepository.save(learningOutcome);
    return learningOutcome.getId();
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

    return new LearningOutcome(competence, Arrays.asList(tool0, tool1), purpose);

  }
}
