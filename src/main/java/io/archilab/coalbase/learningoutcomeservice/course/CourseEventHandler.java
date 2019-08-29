package io.archilab.coalbase.learningoutcomeservice.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.archilab.coalbase.learningoutcomeservice.kafka.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Course.class)
public class CourseEventHandler {

  private final String topic;
  private final KafkaMessageProducer kafkaMessageProducer;

  @Autowired
  public CourseEventHandler(@Value("${course.topic}") final String topic,
      KafkaMessageProducer kafkaMessageProducer) {
    this.topic = topic;
    this.kafkaMessageProducer = kafkaMessageProducer;
  }

  @HandleBeforeCreate
  public void setAuthorBeforeCreate(Course course) {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    course.setAuthor(name);
  }

  @HandleAfterCreate
  public void handleCourseCreate(Course course)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildCourseDomainEvent(course, CourseEventType.CREATED));
  }

  @HandleAfterSave
  public void handleCourseSave(Course course)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildCourseDomainEvent(course, CourseEventType.UPDATED));
  }

  @HandleAfterDelete
  public void handleCourseDelete(Course course)
      throws JsonProcessingException {
    this.kafkaMessageProducer.send(this.topic,
        this.buildCourseDomainEvent(course, CourseEventType.DELETED));
  }

  private CourseDomainEvent buildCourseDomainEvent(
      Course course, CourseEventType eventType) {
    return new CourseDomainEvent(course.getId(), eventType);
  }
}
