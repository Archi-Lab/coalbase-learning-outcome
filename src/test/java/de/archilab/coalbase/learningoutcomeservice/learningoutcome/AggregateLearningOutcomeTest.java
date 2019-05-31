package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bouncycastle.cert.ocsp.Req;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = BEFORE_CLASS)
@AutoConfigureMockMvc
@Transactional
public class AggregateLearningOutcomeTest {

  private static final String TOPIC = "learning-outcome";

  @ClassRule
  public static final EmbeddedKafkaRule BROKER = new EmbeddedKafkaRule(1,
      false, AggregateLearningOutcomeTest.TOPIC);

  private static BlockingQueue<ConsumerRecord<String, String>> records;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @BeforeClass
  public static void setup() {
    System.setProperty("spring.kafka.bootstrap-servers",
        AggregateLearningOutcomeTest.BROKER.getEmbeddedKafka().getBrokersAsString());

    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("testT", "false",
            AggregateLearningOutcomeTest.BROKER.getEmbeddedKafka());

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(
        AggregateLearningOutcomeTest.TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    AggregateLearningOutcomeTest.records = new LinkedBlockingQueue<>();
    container.setupMessageListener(
        (MessageListener<String, String>) record -> AggregateLearningOutcomeTest.records
            .add(record));

    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils
        .waitForAssignment(container,
            AggregateLearningOutcomeTest.BROKER.getEmbeddedKafka()
                .getPartitionsPerTopic());

  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void createLearningOutcome() throws Exception {

    LearningOutcome learningOutcomeToPost = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPost);

    this.mvc.perform(post("/learningOutcomes").with(csrf()).content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(
            jsonPath("$.role.value", is(learningOutcomeToPost.getRole().getValue())))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcomeToPost.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcomeToPost.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.requirements[0].value", is(learningOutcomeToPost.getRequirements().get(0).getValue())))
        .andExpect(
            jsonPath("$.abilities[0].value", is(learningOutcomeToPost.getAbilities().get(0).getValue())))
        .andExpect(
            jsonPath("$.abilities[1].value", is(learningOutcomeToPost.getAbilities().get(1).getValue())))
        .andExpect(jsonPath("$.purposes[0].value", is(learningOutcomeToPost.getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();
    assertThat(learningOutcomes.isEmpty()).isFalse();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getId());
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertThat(savedLearningOutcome.getRole())
        .isEqualTo(learningOutcomeToPost.getRole());
    assertThat(savedLearningOutcome.getCompetence())
        .isEqualTo(learningOutcomeToPost.getCompetence());
    assertThat(savedLearningOutcome.getRequirements().get(0))
        .isEqualTo(learningOutcomeToPost.getRequirements().get(0));
    assertThat(savedLearningOutcome.getAbilities().get(1))
        .isEqualTo(learningOutcomeToPost.getAbilities().get(1));
    assertThat(savedLearningOutcome.getAbilities().get(0))
        .isEqualTo(learningOutcomeToPost.getAbilities().get(0));
    assertThat(savedLearningOutcome.getPurposes().get(0))
        .isEqualTo(learningOutcomeToPost.getPurposes().get(0));

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningOutcomeTest.records
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
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void putLearningOutcome() throws Exception {
    LearningOutcome learningOutcomeToPut = this.buildSampleLearningOutcome();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(learningOutcomeToPut);

    this.mvc.perform(put("/learningOutcomes/" + learningOutcomeToPut.getId().toString())
        .content(json).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(
            jsonPath("$.role.value", is(learningOutcomeToPut.getRole().getValue())))
        .andExpect(
            jsonPath("$.competence.action", is(learningOutcomeToPut.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcomeToPut.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.requirements[0].value", is(learningOutcomeToPut.getRequirements().get(0).getValue())))
        .andExpect(
            jsonPath("$.abilities[0].value", is(learningOutcomeToPut.getAbilities().get(0).getValue())))
        .andExpect(
            jsonPath("$.abilities[1].value", is(learningOutcomeToPut.getAbilities().get(1).getValue())))
        .andExpect(jsonPath("$.purposes[0].value", is(learningOutcomeToPut.getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();
    assertThat(learningOutcomes.isEmpty()).isFalse();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(learningOutcomes.get(0).getId());
    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome savedLearningOutcome = optionalLearningOutcome.get();
    assertThat(savedLearningOutcome.getId()).isEqualTo(learningOutcomeToPut.getId());
    assertThat(savedLearningOutcome.getRole())
            .isEqualTo(learningOutcomeToPut.getRole());
    assertThat(savedLearningOutcome.getCompetence())
            .isEqualTo(learningOutcomeToPut.getCompetence());
    assertThat(savedLearningOutcome.getRequirements().get(0))
            .isEqualTo(learningOutcomeToPut.getRequirements().get(0));
    assertThat(savedLearningOutcome.getAbilities().get(1))
            .isEqualTo(learningOutcomeToPut.getAbilities().get(1));
    assertThat(savedLearningOutcome.getAbilities().get(0))
            .isEqualTo(learningOutcomeToPut.getAbilities().get(0));
    assertThat(savedLearningOutcome.getPurposes().get(0))
            .isEqualTo(learningOutcomeToPut.getPurposes().get(0));

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningOutcomeTest.records
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
  @WithMockUser(username = "testProfessor", roles = {"professor"})
  public void patchLearningOutcome() throws Exception {
    UniqueId<LearningOutcome> identifier = this.createLearningOutcomeToRepo();
    Optional<LearningOutcome> optionalLearningOutcome = this.learningOutcomeRepository
        .findById(identifier);

    assertThat(optionalLearningOutcome.isPresent()).isTrue();
    LearningOutcome learningOutcome = optionalLearningOutcome.orElse(null);
    assertThat(learningOutcome).isNotNull();

    Competence competence = new Competence("Action", TaxonomyLevel.ANALYSIS);
    learningOutcome.setCompetence(competence);

    Ability ability = new Ability("Ability", TaxonomyLevel.ANALYSIS);
    learningOutcome.addAbility(ability);

    Purpose purpose = new Purpose("Purpose", TaxonomyLevel.SYNTHESIS);
    learningOutcome.addPurpose(purpose);

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
            jsonPath("$.abilities[0].value", is(learningOutcome.getAbilities().get(0).getValue())))
        .andExpect(
            jsonPath("$.abilities[1].value", is(learningOutcome.getAbilities().get(1).getValue())))
        .andExpect(
            jsonPath("$.abilities[2].value", is(learningOutcome.getAbilities().get(2).getValue())))
        .andExpect(jsonPath("$.purposes[0].value", is(learningOutcome.getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateLearningOutcomeTest.records
        .poll(10, TimeUnit.SECONDS);
    LearningOutcomeDomainEvent learningOutcomeDomainEvent = objectMapper
        .readValue(record.value(), LearningOutcomeDomainEvent.class);
    assertThat(learningOutcomeDomainEvent.getEventType())
        .isEqualTo(LearningOutcomeEventType.UPDATED.name());
    assertThat(learningOutcomeDomainEvent.getLearningOutcomeIdentifier())
        .isEqualTo(learningOutcome.getId());
  }

  @Test
  @WithMockUser(username = "testProfessor", roles = {"professor"})
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
    ConsumerRecord<String, String> record = AggregateLearningOutcomeTest.records
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
        .andExpect(
            jsonPath("$.role.value", is(learningOutcome.getRole().getValue())))
        .andExpect(jsonPath("$.competence.action", is(learningOutcome.getCompetence().getAction())))
        .andExpect(jsonPath("$.competence.taxonomyLevel",
            is(learningOutcome.getCompetence().getTaxonomyLevel().name())))
        .andExpect(
            jsonPath("$.requirements[0].value", is(learningOutcome.getRequirements().get(0).getValue())))
        .andExpect(jsonPath("$.abilities[0].value", is(learningOutcome.getAbilities().get(0).getValue())))
        .andExpect(jsonPath("$.abilities[1].value", is(learningOutcome.getAbilities().get(1).getValue())))
        .andExpect(jsonPath("$.purposes[0].value", is(learningOutcome.getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._links.self", notNullValue()));

  }

  @Test
  public void getLearningOutcomes() throws Exception {
    this.createLearningOutcomeToRepo();
    this.createLearningOutcomeToRepo();

    List<LearningOutcome> learningOutcomes = (List<LearningOutcome>) this.learningOutcomeRepository
        .findAll();

    String url = "/learningOutcomes/";

    this.mvc.perform(get(url).with(csrf())).andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].role.value",
            is(learningOutcomes.get(0).getRole().getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].competence.action",
            is(learningOutcomes.get(0).getCompetence().getAction())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].competence.taxonomyLevel",
            is(learningOutcomes.get(0).getCompetence().getTaxonomyLevel().name())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].requirements[0].value",
            is(learningOutcomes.get(0).getRequirements().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].abilities[0].value",
            is(learningOutcomes.get(0).getAbilities().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].abilities[1].value",
            is(learningOutcomes.get(0).getAbilities().get(1).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0].purposes[0].value",
            is(learningOutcomes.get(0).getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[0]._links.self", notNullValue()))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].role.value",
            is(learningOutcomes.get(1).getRole().getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].competence.action",
            is(learningOutcomes.get(1).getCompetence().getAction())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].competence.taxonomyLevel",
            is(learningOutcomes.get(1).getCompetence().getTaxonomyLevel().name())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].requirements[0].value",
            is(learningOutcomes.get(1).getRequirements().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].abilities[0].value",
            is(learningOutcomes.get(1).getAbilities().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].abilities[1].value",
            is(learningOutcomes.get(1).getAbilities().get(1).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1].purposes[0].value",
            is(learningOutcomes.get(1).getPurposes().get(0).getValue())))
        .andExpect(jsonPath("$._embedded.learningOutcomes[1]._links.self", notNullValue()));

  }

  private UniqueId<LearningOutcome> createLearningOutcomeToRepo() {
    final LearningOutcome learningOutcome = this.buildSampleLearningOutcome();
    this.learningOutcomeRepository.save(learningOutcome);
    return learningOutcome.getId();
  }

  private LearningOutcome buildSampleLearningOutcome() {
    Role role = new Role("Student");

    Competence competence = new Competence(
            "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
            TaxonomyLevel.SYNTHESIS);

    Requirement requirement = new Requirement("Alles können", TaxonomyLevel.EVALUATION);

    Ability ability0 = new Ability(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren", TaxonomyLevel.SYNTHESIS);
    Ability ability1 = new Ability(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen", TaxonomyLevel.SYNTHESIS);
    Purpose purpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten", TaxonomyLevel.APPLICATION);

    return new LearningOutcome(role, competence, Arrays.asList(requirement), Arrays.asList(ability0, ability1), Arrays.asList(purpose));

  }
}
