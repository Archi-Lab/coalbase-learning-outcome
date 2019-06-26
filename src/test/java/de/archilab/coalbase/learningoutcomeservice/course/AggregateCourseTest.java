package de.archilab.coalbase.learningoutcomeservice.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.archilab.coalbase.learningoutcomeservice.examform.Duration;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamDescription;
import de.archilab.coalbase.learningoutcomeservice.examform.ExamType;
import de.archilab.coalbase.learningoutcomeservice.examform.Schedule;
import de.archilab.coalbase.learningoutcomeservice.learningspace.ExamForm;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpace;
import de.archilab.coalbase.learningoutcomeservice.learningspace.LearningSpaceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@Transactional
public class AggregateCourseTest {

  private static final String COURSE_TOPIC = "course";
  @ClassRule
  public static final EmbeddedKafkaRule BROKER = new EmbeddedKafkaRule(1, false,
      AggregateCourseTest.COURSE_TOPIC);
  private static final String SHORT_TITLE = "createCourseTestShortTitle";
  private static final String TITLE = "createCourseTestTitle";
  private static final String DESCRIPTION = "create Course Test Description";
  private static final String AUTHOR = "testProfessor";
  private static BlockingQueue<ConsumerRecord<String, String>> records;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningSpaceRepository learningSpaceRepository;

  @Autowired
  private CourseRepository courseRepository;

  @BeforeClass
  public static void setupKafka() {
    System.setProperty("spring.kafka.bootstrap-servers",
        AggregateCourseTest.BROKER.getEmbeddedKafka().getBrokersAsString());

    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("testT", "false",
            AggregateCourseTest.BROKER.getEmbeddedKafka());

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(
        AggregateCourseTest.COURSE_TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    AggregateCourseTest.records = new LinkedBlockingQueue<>();
    container.setupMessageListener(
        (MessageListener<String, String>) record -> AggregateCourseTest.records
            .add(record));

    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils
        .waitForAssignment(container,
            AggregateCourseTest.BROKER.getEmbeddedKafka()
                .getPartitionsPerTopic());

  }

  @Test
  @WithMockUser(username = AUTHOR, roles = {"professor"})
  public void createCourseExpectCreatedWithPost() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));

    String courseAsJsonString = this.objectMapper.writeValueAsString(course);
    JSONObject courseJsonObject = new JSONObject(courseAsJsonString);
    JSONArray jsonArray = new JSONArray(Arrays.asList("learningSpaces/" + learningSpace.getId()));
    courseJsonObject.put("learningSpaces", jsonArray);

    this.mvc.perform(post("/courses").with(csrf()).content(courseJsonObject.toString()).contentType(
        MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.title", is(course.getTitle())))
        .andExpect(jsonPath("$.description", is(course.getDescription())))
        .andExpect(jsonPath("$._links.learningSpaces", notNullValue()))
        .andExpect(jsonPath("$._links.self", notNullValue()));
    SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());

    final List<Course> courses = (List<Course>) this.courseRepository.findAll();
    assertThat(courses).isNotNull().hasSize(1);
    Course savedCourse = courses.get(0);
    assertThat(savedCourse.getId()).isNotNull();
    assertThat(savedCourse.getTitle()).isEqualTo(TITLE);
    assertThat(savedCourse.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(savedCourse.getLearningSpaces()).isNotNull().isNotEmpty().hasSize(1);
    assertThat(savedCourse.getLearningSpaces().get(0)).isEqualTo(learningSpace);

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateCourseTest.records
        .poll(100, TimeUnit.MILLISECONDS);

    CourseDomainEvent courseDomainEvent = this.objectMapper
        .readValue(record.value(), CourseDomainEvent.class);
    assertThat(courseDomainEvent).isNotNull();
    assertThat(courseDomainEvent.getEventID()).isNotNull();
    assertThat(courseDomainEvent.getCourseIdentifier()).isNotNull().isEqualTo(savedCourse.getId());
    assertThat(courseDomainEvent.getEventType()).isNotNull()
        .isEqualTo(CourseEventType.CREATED.toString());
  }

  @Test
  @WithMockUser(username = AUTHOR, roles = {"professor"})
  public void createCourseExpectCreateWithPut() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    course.setAuthor(AUTHOR);
    String courseAsJsonString = this.objectMapper.writeValueAsString(course);
    JSONObject courseJsonObject = new JSONObject(courseAsJsonString);
    JSONArray jsonArray = new JSONArray(Arrays.asList("learningSpaces/" + learningSpace.getId()));
    courseJsonObject.put("learningSpaces", jsonArray);

    this.mvc.perform(
        put("/courses/" + course.getId()).with(csrf()).content(courseJsonObject.toString())
            .contentType(
                MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.title", is(course.getTitle())))
        .andExpect(jsonPath("$.description", is(course.getDescription())))
        .andExpect(jsonPath("$._links.learningSpaces", notNullValue()))
        .andExpect(jsonPath("$._links.self", notNullValue()));
    SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());

    final List<Course> courses = (List<Course>) this.courseRepository.findAll();
    assertThat(courses).isNotNull().hasSize(1);
    Course savedCourse = courses.get(0);
    assertThat(savedCourse.getId()).isNotNull();
    assertThat(savedCourse.getTitle()).isEqualTo(TITLE);
    assertThat(savedCourse.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(savedCourse.getLearningSpaces()).isNotNull().isNotEmpty().hasSize(1);
    assertThat(savedCourse.getLearningSpaces().get(0)).isEqualTo(learningSpace);

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateCourseTest.records
        .poll(10, TimeUnit.SECONDS);

    CourseDomainEvent courseDomainEvent = this.objectMapper
        .readValue(record.value(), CourseDomainEvent.class);
    assertThat(courseDomainEvent).isNotNull();
    assertThat(courseDomainEvent.getEventID()).isNotNull();
    assertThat(courseDomainEvent.getCourseIdentifier()).isNotNull().isEqualTo(savedCourse.getId());
    assertThat(courseDomainEvent.getEventType()).isNotNull()
        .isEqualTo(CourseEventType.CREATED.toString());
  }

  @Test
  @WithMockUser(username = AUTHOR, roles = {"professor"})
  public void updateCourseExpectedUpdatedWithPatch() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    course.setAuthor(AUTHOR);
    this.courseRepository.save(course);

    LearningSpace learningSpace2 = this.createLearningSpace();
    String titleChanged = "createCourseTestTitle2";
    String descriptionChanged = "create Course Test Description2";

    course.setTitle(titleChanged);
    course.setDescription(descriptionChanged);
    course.addLearningSpace(learningSpace2);

    String courseAsJsonString = this.objectMapper.writeValueAsString(course);
    JSONObject courseJsonObject = new JSONObject(courseAsJsonString);
    JSONArray jsonArray = new JSONArray(Arrays.asList("learningSpaces/" + learningSpace.getId(),
        "learningSpaces/" + learningSpace2.getId()));
    courseJsonObject.put("learningSpaces", jsonArray);
    this.mvc.perform(
        patch("/courses/" + course.getId()).with(csrf()).content(courseJsonObject.toString())
            .contentType(
                MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(titleChanged)))
        .andExpect(jsonPath("$.description", is(descriptionChanged)))
        .andExpect(jsonPath("$._links.learningSpaces", notNullValue()))
        .andExpect(jsonPath("$._links.self", notNullValue()));
    SecurityContextHolder.setContext(TestSecurityContextHolder.getContext());

    final List<Course> courses = (List<Course>) this.courseRepository.findAll();
    assertThat(courses).isNotNull().hasSize(1);
    Course savedCourse = courses.get(0);
    assertThat(savedCourse.getId()).isNotNull();
    assertThat(savedCourse.getTitle()).isEqualTo(titleChanged);
    assertThat(savedCourse.getDescription()).isEqualTo(descriptionChanged);
    assertThat(savedCourse.getLearningSpaces()).isNotNull().isNotEmpty().hasSize(2);
    assertThat(savedCourse.getLearningSpaces().get(0)).isEqualTo(learningSpace);
    assertThat(savedCourse.getLearningSpaces().get(1)).isEqualTo(learningSpace2);
    ConsumerRecord<String, String> record = AggregateCourseTest.records
        .poll(10, TimeUnit.SECONDS);

    CourseDomainEvent courseDomainEvent = this.objectMapper
        .readValue(record.value(), CourseDomainEvent.class);
    assertThat(courseDomainEvent).isNotNull();
    assertThat(courseDomainEvent.getEventID()).isNotNull();
    assertThat(courseDomainEvent.getCourseIdentifier()).isNotNull().isEqualTo(savedCourse.getId());
    assertThat(courseDomainEvent.getEventType()).isNotNull()
        .isEqualTo(CourseEventType.UPDATED.toString());
  }


  @Test
  @WithMockUser(username = AUTHOR, roles = {"professor"})
  public void deleteCourseExpectDeleted() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    course.setAuthor(AUTHOR);
    this.courseRepository.save(course);

    this.mvc.perform(
        delete("/courses/" + course.getId()).with(csrf())).andExpect(status().isNoContent());

    Optional<Course> savedCourseOpt = this.courseRepository.findById(course.getId());
    Course savedCourse = null;
    assertThat(savedCourseOpt.isPresent()).isFalse();
    if (savedCourseOpt.isPresent()) {
      savedCourse = savedCourseOpt.get();
    }
    assertThat(savedCourse).isNull();

    /*Test kafka message */
    ConsumerRecord<String, String> record = AggregateCourseTest.records
        .poll(10, TimeUnit.SECONDS);

    CourseDomainEvent courseDomainEvent = this.objectMapper
        .readValue(record.value(), CourseDomainEvent.class);
    assertThat(courseDomainEvent).isNotNull();
    assertThat(courseDomainEvent.getEventID()).isNotNull();
    assertThat(courseDomainEvent.getCourseIdentifier()).isNotNull().isEqualTo(course.getId());
    assertThat(courseDomainEvent.getEventType()).isNotNull()
        .isEqualTo(CourseEventType.DELETED.toString());
  }

  @Test
  public void createCourseExpectNotAuthorized() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));

    String courseAsJsonString = this.objectMapper.writeValueAsString(course);

    this.mvc.perform(post("/courses/").with(csrf()).content(courseAsJsonString).contentType(
        MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  public void updateCourseExpectNotAuthorized() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));

    String courseAsJsonString = this.objectMapper.writeValueAsString(course);

    this.mvc.perform(
        put("/courses/" + course.getId()).with(csrf()).content(courseAsJsonString).contentType(
            MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  public void updateCourseExpectNotAuthorizedWithPatch() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));

    String courseAsJsonString = this.objectMapper.writeValueAsString(course);

    this.mvc.perform(
        patch("/courses/" + course.getId()).with(csrf()).content(courseAsJsonString).contentType(
            MediaType.APPLICATION_JSON)).andExpect(status().is(401));
  }

  @Test
  public void deleteCourseExpectNotAuthorized() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    this.courseRepository.save(course);

    this.mvc.perform(
        delete("/courses/" + course.getId()).with(csrf())).andExpect(status().is(401));

    Optional<Course> savedCourseOpt = this.courseRepository.findById(course.getId());
    Course savedCourse = null;
    assertThat(savedCourseOpt.isPresent()).isTrue();
    if (savedCourseOpt.isPresent()) {
      savedCourse = savedCourseOpt.get();
    }
    assertThat(savedCourse).isNotNull().isEqualTo(course);
  }

  @Test
  public void getCourseExpectSpecificCourse() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    this.courseRepository.save(course);

    this.mvc.perform(get("/courses/" + course.getId())).andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(course.getTitle())))
        .andExpect(jsonPath("$.description", is(course.getDescription())))
        .andExpect(jsonPath("$._links.learningSpaces", notNullValue()))
        .andExpect(jsonPath("$._links.self", notNullValue()));
  }

  @Test
  @WithMockUser(username = AUTHOR, roles = {"student"})
  public void getCoursesExpectOneCourse() throws Exception {
    LearningSpace learningSpace = this.createLearningSpace();
    Course course = new Course(SHORT_TITLE, TITLE, DESCRIPTION,
        new ArrayList<>(Arrays.asList(learningSpace)));
    course.setAuthor(AUTHOR);
    this.courseRepository.save(course);

    this.mvc.perform(get("/courses")).andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.courses[0].title", is(course.getTitle())))
        .andExpect(jsonPath("$._embedded.courses[0].description", is(course.getDescription())))
        .andExpect(jsonPath("$._embedded.courses[0]._links.learningSpaces", notNullValue()))
        .andExpect(jsonPath("$._embedded.courses[0]._links.self", notNullValue()))
        .andExpect(jsonPath("$._links.self", notNullValue()));
  }

  private LearningSpace createLearningSpace() {
    final LearningSpace learningSpace = new LearningSpace("LearningSpaceTest", createExamForm());
    this.learningSpaceRepository.save(learningSpace);
    return learningSpace;
  }

  private static ExamForm createExamForm() {
    ExamType type = new ExamType("Klausur");

    List<Schedule> schedules = new ArrayList<>();
    schedules.add(new Schedule("Am Anfang"));

    Duration duration = new Duration(10, 15, "Min");

    ExamDescription description = new ExamDescription("Ist einfach");

    return new ExamForm(type, schedules, duration, description);
  }
}
